-- ~/.config/nvim/lua/plugins/lualine.lua

return {
  "nvim-lualine/lualine.nvim",
  dependencies = { "nvim-tree/nvim-web-devicons" }, -- For filetype icons
  config = function()
    require("lualine").setup({
      options = {
        theme = "tokyonight",
        section_separators = { left = '', right = '' },
        component_separators = { left = '', right = '' },
        disabled_filetypes = {},
      },
      sections = {
        lualine_a = { "mode" },
        lualine_b = { "branch", "diff" },
        lualine_c = { { "filename", path = 1 } }, -- Show relative path
        lualine_x = { "diagnostics", "encoding", "filetype" },
        lualine_y = { "progress" },
        lualine_z = { "location" },
      },
    })
  end,
}
