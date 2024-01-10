/// <reference types="vite/client" />


declare module "*.vue" { // ts识别.vue文件
  import type { DefineComponent } from "vue";
 
  const vueComponent: DefineComponent<{}, {}, any>;
 
  export default vueComponent;
}