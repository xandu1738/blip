-- ~/.config/nvim/lua/plugins/colorscheme.lua

return {
  "folke/tokyonight.nvim",
  priority = 1000, -- Make sure this plugin is loaded first
  config = function()
    -- Load the colorscheme
    vim.cmd.colorscheme "tokyonight"
  end,
}
