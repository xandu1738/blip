-- ~/.config/nvim/lua/plugins/nvim-surround.lua

return {
  'kylechui/nvim-surround',
  version = "*", -- Use for stability
  event = "VeryLazy",
  config = function()
    require('nvim-surround').setup({})
  end
}
