-- ~/.config/nvim/lua/core/keymaps.lua

local map = vim.keymap.set

-- -----------------------------------------------------------------------------
-- GENERAL KEYMAPS
-- -----------------------------------------------------------------------------
-- Save the file
map("n", "<leader>w", ":w<CR>", { noremap = true, silent = true, desc = "Save file" })
-- Exit insert mode with `jk`
map("i", "jk", "<ESC>", { noremap = true, silent = true, desc = "Exit insert mode" })

-- -----------------------------------------------------------------------------
-- WINDOW NAVIGATION
-- -----------------------------------------------------------------------------
map("n", "<leader>h", "<C-w>h", { noremap = true, silent = true, desc = "Navigate to left split" })
map("n", "<leader>j", "<C-w>j", { noremap = true, silent = true, desc = "Navigate to below split" })
map("n", "<leader>k", "<C-w>k", { noremap = true, silent = true, desc = "Navigate to above split" })
map("n", "<leader>l", "<C-w>l", { noremap = true, silent = true, desc = "Navigate to right split" })

-- -----------------------------------------------------------------------------
-- SPLIT RESIZING
-- -----------------------------------------------------------------------------
map("n", "<C-Up>", ":resize +2<CR>", { noremap = true, silent = true, desc = "Increase split height" })
map("n", "<C-Down>", ":resize -2<CR>", { noremap = true, silent = true, desc = "Decrease split height" })
map("n", "<C-Left>", ":vertical resize -2<CR>", { noremap = true, silent = true, desc = "Decrease split width" })
map("n", "<C-Right>", ":vertical resize +2<CR>", { noremap = true, silent = true, desc = "Increase split width" })

-- -----------------------------------------------------------------------------
-- BUFFER MANAGEMENT
-- -----------------------------------------------------------------------------
map("n", "<S-l>", ":bnext<CR>", { noremap = true, silent = true, desc = "Next buffer" })
map("n", "<S-h>", ":bprevious<CR>", { noremap = true, silent = true, desc = "Previous buffer" })
map("n", "<leader>bd", ":bdelete<CR>", { noremap = true, silent = true, desc = "Close current buffer" })
