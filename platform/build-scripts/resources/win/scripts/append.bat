IF NOT DEFINED ACC (
  SET ACC=%1
) ELSE IF "%SEPARATOR%" == "" (
  SET ACC=%ACC% %1
) ELSE (
  SET ACC=%ACC%%SEPARATOR%%1
)
