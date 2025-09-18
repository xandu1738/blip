-- ~/.config/nvim/lua/plugins/dap.lua

return {
  'mfussenegger/nvim-dap',
  dependencies = {
    'rcarriga/nvim-dap-ui',
    'theHamsta/nvim-dap-virtual-text',
    'jay-babu/mason-nvim-dap.nvim', -- Installs debug adapters
  },
  config = function()
    local dap = require('dap')
    local dapui = require('dapui')

    -- Configure mason-nvim-dap to install debug adapters
    require('mason-nvim-dap').setup({
      ensure_installed = { 'codelldb', 'java-debug-adapter', 'node-debug2-adapter' },
    })

    dapui.setup()

    -- Open/close DAP UI on debug events
    dap.listeners.after.event_initialized['dapui_config'] = function()
      dapui.open()
    end
    dap.listeners.before.event_terminated['dapui_config'] = function()
      dapui.close()
    end
    dap.listeners.before.event_exited['dapui_config'] = function()
      dapui.close()
    end

    -- Keymaps for debugging
    vim.keymap.set('n', '<leader>db', dap.toggle_breakpoint, { desc = "Toggle breakpoint" })
    vim.keymap.set('n', '<leader>dc', dap.continue, { desc = "Continue" })
    vim.keymap.set('n', '<leader>do', dap.step_over, { desc = "Step over" })
    vim.keymap.set('n', '<leader>di', dap.step_into, { desc = "Step into" })
    vim.keymap.set('n', '<leader>du', dap.step_out, { desc = "Step out" })
    vim.keymap.set('n', '<leader>dr', dap.repl.open, { desc = "Open REPL" })
  end,
}
