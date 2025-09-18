-- ~/.config/nvim/lua/plugins/bufferline.lua

return {
  'akinsho/bufferline.nvim',
  dependencies = {'nvim-tree/nvim-web-devicons'},
  config = function()
    require('bufferline').setup({
      options = {
        mode = "buffers", -- Display buffers
        diagnostics = "nvim_lsp",
        diagnostics_indicator = function(count, level, diagnostics_dict, context)
          return "("..count..")"
        end,
        show_buffer_close_icons = true,
        show_close_icon = true,
      }
    })
  end
}
