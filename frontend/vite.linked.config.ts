import { defineConfig } from 'vite'
import path from 'path'
import tailwindcss from '@tailwindcss/vite'
import react from '@vitejs/plugin-react'

const defaultFrontendRoot = 'C:/Users/data8320-13/Desktop/YKK/YKK-Frontend'
const frontendRoot = process.env.YKK_FRONTEND_DIR || defaultFrontendRoot
const backendStaticRoot = path.resolve(__dirname, '../src/main/resources/static')

function figmaAssetResolver() {
  return {
    name: 'figma-asset-resolver',
    resolveId(id: string) {
      if (id.startsWith('figma:asset/')) {
        return path.resolve(frontendRoot, 'src/assets', id.replace('figma:asset/', ''))
      }
    },
  }
}

function backendLiveReload() {
  return {
    name: 'backend-live-reload',
    transformIndexHtml() {
      return [{
        tag: 'script',
        injectTo: 'body' as const,
        children: `
          let frontendVersion;
          window.setInterval(async () => {
            try {
              const response = await fetch('/frontend-version.txt?t=' + Date.now(), { cache: 'no-store' });
              if (!response.ok) return;
              const nextVersion = await response.text();
              if (frontendVersion === undefined) frontendVersion = nextVersion;
              else if (frontendVersion !== nextVersion) window.location.reload();
            } catch (_) {}
          }, 1000);
        `,
      }]
    },
    generateBundle(this: { emitFile: (asset: { type: 'asset'; fileName: string; source: string }) => void }) {
      this.emitFile({
        type: 'asset',
        fileName: 'frontend-version.txt',
        source: String(Date.now()),
      })
    },
  }
}

export default defineConfig({
  root: frontendRoot,
  plugins: [figmaAssetResolver(), react(), tailwindcss(), backendLiveReload()],
  resolve: {
    alias: {
      '@': path.resolve(frontendRoot, 'src'),
    },
  },
  build: {
    outDir: backendStaticRoot,
    emptyOutDir: true,
  },
  assetsInclude: ['**/*.svg', '**/*.csv'],
})
