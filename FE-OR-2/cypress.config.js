import { defineConfig } from 'cypress'

export default defineConfig({
  e2e: {
    experimentalRunAllSpecs: true,
    specPattern: 'cypress/e2e/**/*.{cy,spec}.{js,jsx,ts,tsx}',
    baseUrl: 'http://localhost:5173',
    excludeSpecPattern: [
      'cypress/e2e/example.cy.js',
      'cypress/e2e/release-1/**/*.cy.js',
      'cypress/e2e/release-2/sprint-1/**/*.cy.js',
      'cypress/e2e/release-2/sprint-2/tc-pbi18-simple-auth/**/*.cy.js',
    ],
  },
  component: {
    specPattern: 'src/**/__tests__/*.{cy,spec}.{js,ts,jsx,tsx}',
    devServer: {
      framework: 'vue',
      bundler: 'vite'
    }
  },
})
