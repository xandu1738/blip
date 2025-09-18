-- ~/.config/nvim/init.lua

-- -----------------------------------------------------------------------------
-- LAZY.NVIM BOOTSTRAP
-- -----------------------------------------------------------------------------
-- This section ensures that the lazy.nvim plugin manager is installed and ready.
-- It will be automatically installed on the first run.
local lazypath = vim.fn.stdpath("data") .. "/lazy/lazy.nvim"
if not vim.loop.fs_stat(lazypath) then
  vim.fn.system({
    "git",
    "clone",
    "--filter=blob:none",
    "https://github.com/folke/lazy.nvim.git",
    "--branch=stable", -- Use the latest stable release
    lazypath,
  })
end
vim.opt.rtp:prepend(lazypath)

-- -----------------------------------------------------------------------------
-- CORE MODULES
-- -----------------------------------------------------------------------------
-- Load core configuration files for options, keymaps, and autocommands.
-- These are fundamental settings that don't belong to a specific plugin.
require("core.options")
require("core.keymaps")
require("core.autocmds")

-- -----------------------------------------------------------------------------
-- PLUGIN SETUP
-- -----------------------------------------------------------------------------
-- The `setup` function tells lazy.nvim where to find your plugin specifications.
-- We point it to the `lua/plugins` directory.
require("lazy").setup("plugins")
