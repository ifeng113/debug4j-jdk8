let r=window.settings.baseURL;const o={baseURL:r},i="debug4j",a={setItem(e,s,t){if(t){let g=this.getItem(t);g[e]=s,this.setItem(t,g)}else{let g=this.getStorage();g[e]=s,window.sessionStorage.setItem(i,JSON.stringify(g))}},getItem(e,s){if(s){let t=this.getItem(s);if(t)return t[e]}return this.getStorage()[e]},getStorage(){return JSON.parse(window.sessionStorage.getItem(i)||"{}")},clear(e,s){let t=this.getStorage();s?delete t[s][e]:delete t[e],window.sessionStorage.setItem(i,JSON.stringify(t))}};export{o as g,a as w};
