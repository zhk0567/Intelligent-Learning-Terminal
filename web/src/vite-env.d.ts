/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_STATIC_ORIGIN?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
