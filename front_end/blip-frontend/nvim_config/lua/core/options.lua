-- ~/.config/nvim/lua/core/options.lua

vim.g.mapleader = " " -- Set the leader key to space

local opt = vim.opt -- A shorthand for vim.opt

-- -----------------------------------------------------------------------------
-- EDITOR APPEARANCE
-- -----------------------------------------------------------------------------
opt.relativenumber = true -- Show relative line numbers
opt.number = true         -- Show the absolute line number for the current line
opt.cursorline = true     -- Highlight the current line
opt.termguicolors = true  -- Enable 24-bit RGB color in the TUI

-- -----------------------------------------------------------------------------
-- INDENTATION
-- -----------------------------------------------------------------------------
opt.expandtab = true     -- Use spaces instead of tabs
opt.shiftwidth = 4       -- Size of an indent
opt.tabstop = 4          -- Number of spaces a tab character counts for
opt.softtabstop = 4      -- Number of spaces to insert for a tab

-- -----------------------------------------------------------------------------
-- SEARCH
-- -----------------------------------------------------------------------------
opt.smartcase = true     -- Case-sensitive search if the query contains uppercase letters
opt.ignorecase = true    -- Case-insensitive search otherwise

-- -----------------------------------------------------------------------------
-- CLIPBOARD
-- -----------------------------------------------------------------------------
opt.clipboard = "unnamedplus" -- Use the system clipboard for yank and paste

-- -----------------------------------------------------------------------------
-- OTHER
-- -----------------------------------------------------------------------------
opt.scrolloff = 8         -- Keep 8 lines of context around the cursor
opt.sidescrolloff = 8
opt.mouse = "a"           -- Enable mouse support in all modes
