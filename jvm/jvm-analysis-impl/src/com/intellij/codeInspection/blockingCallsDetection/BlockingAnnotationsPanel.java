// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.codeInspection.blockingCallsDetection;

import com.intellij.analysis.JvmAnalysisBundle;
import com.intellij.codeInsight.AnnotationTargetUtil;
import com.intellij.codeInspection.ui.ListWrappingTableModel;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.ClassFilter;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.actionSystem.ShortcutSet;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.ui.CommonActionsPanel;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.StatusText;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static com.intellij.util.ui.JBUI.Panels.simplePanel;

class BlockingAnnotationsPanel {
  private final Project myProject;
  private final Set<String> myDefaultAnnotations;
  private final JBTable myTable;
  private final JPanel myComponent;
  protected final ListWrappingTableModel myTableModel;
  private final @NlsContexts.StatusText String myCustomEmptyText;
  private final @NlsContexts.StatusText String myCustomAddLinkText;

  BlockingAnnotationsPanel(Project project,
                           @NlsContexts.ColumnName String name,
                           List<String> annotations,
                           List<String> defaultAnnotations,
                           @NlsContexts.StatusText String customEmptyText,
                           @NlsContexts.StatusText String customAddLinkText) {
    myProject = project;
    myDefaultAnnotations = new HashSet<>(defaultAnnotations);
    myCustomEmptyText = customEmptyText;
    myCustomAddLinkText = customAddLinkText;
    myTableModel = new ListWrappingTableModel(annotations, name) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return column == 1;
      }
    };

    DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
    columnModel.addColumn(new TableColumn(0, 100, new ColoredTableCellRenderer() {
      @Override
      public void acquireState(JTable table, boolean isSelected, boolean hasFocus, int row, int column) {
        super.acquireState(table, isSelected, false, row, column);
      }

      @Override
      protected void customizeCellRenderer(@NotNull JTable table,
                                           Object value,
                                           boolean selected,
                                           boolean hasFocus,
                                           int row,
                                           int column) {
        if (value == null) return;
        if (!isAnnotationAccessible((String)value)) {
          append((String)value, SimpleTextAttributes.GRAYED_ATTRIBUTES);
          setIcon(AllIcons.General.Warning);
        }
        else {
          append((String)value, SimpleTextAttributes.REGULAR_ATTRIBUTES);
          setIcon(AllIcons.Nodes.Annotationtype);
        }
      }
    }, null));

    myTable = new JBTable(myTableModel, columnModel) {
      @NotNull
      @Override
      public StatusText getEmptyText() {
        StatusText emptyText = super.getEmptyText();
        if (!myProject.isDefault() && myCustomEmptyText != null && myCustomAddLinkText != null) {
          emptyText.setText(myCustomEmptyText)
            .appendSecondaryText(myCustomAddLinkText, SimpleTextAttributes.LINK_ATTRIBUTES, e -> chooseAnnotation(name));

          ShortcutSet shortcutSet = CommonActionsPanel.getCommonShortcut(CommonActionsPanel.Buttons.ADD);
          String shortcutText = KeymapUtil.getFirstKeyboardShortcutText(shortcutSet);
          emptyText.appendSecondaryText(" (" + shortcutText + ")", StatusText.DEFAULT_ATTRIBUTES, null);
        }
        return emptyText;
      }
    };

    ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(myTable);
    toolbarDecorator
      .setAddAction(actionButton -> chooseAnnotation(name))
      .setRemoveAction(actionButton ->  {
        String selectedValue = getSelectedAnnotation();
        if (selectedValue == null) return;
        myTableModel.removeRow(myTable.getSelectedRow());
      })
      .setRemoveActionUpdater(e -> !myProject.isDefault() && !myDefaultAnnotations.contains(getSelectedAnnotation()))
      .setAddActionUpdater(e -> !myProject.isDefault());

    JPanel panel = toolbarDecorator.createPanel();
    myComponent = new JPanel(new BorderLayout());
    BorderLayoutPanel withBorder = simplePanel()
      .addToTop(simplePanel(new JLabel(name + ":")).withBorder(empty(10, 0)))
      .addToCenter(panel);
    myComponent.add(withBorder, BorderLayout.CENTER);

    myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    myTable.setRowSelectionAllowed(true);
    myTable.setVisibleRowCount(3);
    myTable.setShowGrid(false);
  }

  private boolean isAnnotationAccessible(String annotationFqn) {
    return JavaPsiFacade.getInstance(myProject).findClass(annotationFqn, GlobalSearchScope.allScope(myProject)) != null;
  }

  private void addRow(String annotation) {
    myTableModel.addRow(annotation);
  }

  private Integer selectAnnotation(String annotation) {
    for (int i = 0; i < myTable.getRowCount(); i++) {
      if (annotation.equals(myTable.getValueAt(i, 0))) {
        myTable.setRowSelectionInterval(i, i);
        return i;
      }
    }
    return null;
  }

  private String getSelectedAnnotation() {
    int selectedRow = myTable.getSelectedRow();
    return selectedRow < 0 ? null : (String)myTable.getValueAt(selectedRow, 0);
  }

  private void chooseAnnotation(String title) {
    final TreeClassChooser chooser = TreeClassChooserFactory.getInstance(myProject)
      .createNoInnerClassesScopeChooser(JvmAnalysisBundle.message("dialog.title.choose.annotation", title), GlobalSearchScope.allScope(myProject), new ClassFilter() {
        @Override
        public boolean isAccepted(PsiClass aClass) {
          return PsiAnnotation.TargetType.METHOD.equals(AnnotationTargetUtil.findAnnotationTarget(aClass, PsiAnnotation.TargetType.METHOD));
        }
      }, null);
    chooser.showDialog();
    final PsiClass selected = chooser.getSelected();
    if (selected == null) {
      return;
    }
    final String qualifiedName = selected.getQualifiedName();
    if (selectAnnotation(qualifiedName) == null) {
      addRow(qualifiedName);
    }
  }

  public JComponent getComponent() {
    return myComponent;
  }

  public String[] getAnnotations() {
    int size = myTable.getRowCount();
    String[] result = new String[size];
    for (int i = 0; i < size; i++) {
      result[i] = (String)myTable.getValueAt(i, 0);
    }
    return result;
  }
}