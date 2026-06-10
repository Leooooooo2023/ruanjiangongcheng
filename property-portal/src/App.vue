<template>
  <router-view />
</template>

<script setup>
</script>

<style>
:root {
  --brand: #409eff;
  --brand-dark: #1d6fd8;
  --brand-soft: #ecf5ff;
  --ink: #1f2937;
  --muted: #6b7280;
  --line: #e5eaf3;
  --surface: #ffffff;
  --page: #f4f7fb;
  --shadow: 0 12px 32px rgba(31, 45, 61, 0.08);
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  color: var(--ink);
  background: var(--page);
}

#app {
  min-height: 100vh;
}

.el-card {
  border: 1px solid var(--line);
  border-radius: 8px;
  box-shadow: var(--shadow);
}

.el-button {
  border-radius: 6px;
  font-weight: 600;
  transition: transform 0.18s ease, box-shadow 0.18s ease, background-color 0.18s ease, border-color 0.18s ease;
}

.el-button:not(.is-disabled):not(.is-loading):hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 18px rgba(64, 158, 255, 0.22);
}

.el-button:not(.is-disabled):not(.is-loading):active {
  transform: translateY(0) scale(0.98);
  box-shadow: 0 4px 10px rgba(64, 158, 255, 0.16);
}

.el-button--warning:not(.is-disabled):not(.is-loading):hover {
  box-shadow: 0 8px 18px rgba(230, 162, 60, 0.22);
}

.el-input__wrapper,
.el-select__wrapper,
.el-textarea__inner {
  border-radius: 6px;
  transition: box-shadow 0.18s ease;
}

.el-input__wrapper.is-focus,
.el-select__wrapper.is-focused,
.el-textarea__inner:focus {
  box-shadow: 0 0 0 1px var(--brand) inset, 0 0 0 4px rgba(64, 158, 255, 0.12);
}
</style>
