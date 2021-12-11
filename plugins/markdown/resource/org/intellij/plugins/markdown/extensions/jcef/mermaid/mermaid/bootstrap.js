// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
(function() {
  function checkAllLoaded(blocks) {
    return new Promise(resolve => {
      if (blocks.length === 0) {
        resolve();
        return;
      }
      const interval = setInterval(() => {
        if (blocks.every(block => block.querySelector("svg") != null)) {
          clearInterval(interval);
          resolve();
        }
      }, 20);
    });
  }

  async function renderBlock(block, cacheId, content) {
    return new Promise(resolve => {
      try {
        mermaid.render(`mermaid-generated-${cacheId}`, content, generated => {
          console.log(`Generated by mermaid for cache-id: ${cacheId}`);
          block.innerHTML = generated;
          resolve(generated);
        }, block);
      } catch (error) {
        console.error(error);
        resolve(null);
      }
    });
  }

  async function renderBlocks(blocks) {
    const promises = [];
    for (const block of blocks) {
      block.removeAttribute("data-processed");
      const cacheId = block.getAttribute("data-cache-id");
      const actualContent = atob(block.getAttribute("data-actual-fence-content"));
      const promise = renderBlock(block, cacheId, actualContent).then(generated => {
        if (generated != null) {
          window.__IntelliJTools.messagePipe.post("storeMermaidFile", `${cacheId};${generated}`);
        }
      });
      promises.push(promise);
    }
    return Promise.all(promises);
  }

  async function renderAll() {
    console.time("mermaid-init");
    console.log("Calling mermaid init");
    console.log(`Applying mermaid theme: ${window.mermaidTheme}`);
    mermaid.mermaidAPI.initialize({theme: window.mermaidTheme});
    const blocks = Array.from(document.querySelectorAll("div.mermaid"));
    console.log(`${blocks.length} blocks will be generated by mermaid`);
    await renderBlocks(blocks);
    console.log(`Generated all mermaid blocks`);
  }

  IncrementalDOM.notifications.afterPatchListeners.push(renderAll);
  void renderAll();
})();