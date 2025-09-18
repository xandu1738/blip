-- ~/.config/nvim/lua/plugins/lsp.lua

return {
  "neovim/nvim-lspconfig",
  dependencies = {
    "williamboman/mason.nvim",
    "williamboman/mason-lspconfig.nvim",
  },
  config = function()
    -- Import mason and mason-lspconfig
    local mason = require("mason")
    local mason_lspconfig = require("mason-lspconfig")
    local lspconfig = require("lspconfig")

    -- Enable mason
    mason.setup()

    -- Define which LSPs to install
    mason_lspconfig.setup({
      ensure_installed = {
        "jdtls",
        "clangd",
        "tsserver",
        "html",
        "cssls",
        "tailwindcss",
      },
    })

    -- Set up a handler for each LSP server
    mason_lspconfig.setup_handlers({
      -- The default handler for most servers
      function(server_name)
        lspconfig[server_name].setup({
          -- Use capabilities from nvim-cmp
          capabilities = require("cmp_nvim_lsp").default_capabilities(),
        })
      end,

      -- Custom setup for Java (jdtls)
      ["jdtls"] = function()
        -- See https://github.com/mfussenegger/nvim-jdtls for more details
        lspconfig.jdtls.setup({
          capabilities = require("cmp_nvim_lsp").default_capabilities(),
          -- Add any Java-specific configurations here
        })
      end,
    })

    -- LSP-related keymaps
    vim.keymap.set('n', 'K', vim.lsp.buf.hover, { desc = "LSP Hover" })
    vim.keymap.set('n', 'gd', vim.lsp.buf.definition, { desc = "Go to definition" })
    vim.keymap.set('n', 'gr', vim.lsp.buf.references, { desc = "Go to references" })
    vim.keymap.set('n', '<leader>ca', vim.lsp.buf.code_action, { desc = "Code action" })
  end,
}
