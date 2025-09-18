-- ~/.config/nvim/lua/core/autocmds.lua

local augroup = vim.api.nvim_create_augroup
local autocmd = vim.api.nvim_create_autocmd

-- -----------------------------------------------------------------------------
-- FORMAT ON SAVE
-- -----------------------------------------------------------------------------
-- This autocommand group will automatically format your code when you save.
-- It uses the attached LSP server, so it will work for any configured language.
local format_on_save_group = augroup("FormatOnSave", { clear = true })
autocmd("BufWritePre", {
  group = format_on_save_group,
  pattern = "*",
  callback = function()The dashboard will use a modern, two-column layout.

   * Left Column (Main Content): Will contain dynamic data like charts and activity
     lists that are frequently updated.
   * Right Column (Contextual Information): Will display more static information about
     the club and provide quick links for common actions.

    1 +------------------------------------------------------------------------
      --+
    2 | Rotary Club of [Club Name] - District [District]
      |
    3 +------------------------------------------------------------------------
      --+
    4 |
      |
    5 |  [Stat Card: Total Members] [Stat Card: Active Members] [Stat Card:
      Avg. Attendance] |
    6 |
      |
    7 +-------------------------------------------+----------------------------
      --+
    8 |                                           |
      |
    9 |   <-- LEFT COLUMN -->                     |   <-- RIGHT COLUMN -->
      |
   10 |                                           |
      |
   11 |   [Chart: Meeting Attendance Trends]      |   [Panel: Club Details]
      |
   12 |                                           |
      |
   13 |   [Table: Recent Member Activity]         |   [List: Upcoming Meetings]
      |
   14 |                                           |
      |
   15 |                                           |   [Section: Quick Actions]
      |
   16 |                                           |
      |
   17 +-------------------------------------------+----------------------------
      --+

  3. Component Breakdown

  Here are the features that will deliver this experience, based on the entities we
  explored:

  A. Stat Cards (Top Row)

  These provide immediate insight into the most important metrics.

   * Total Members:
       * UI: A card displaying a large number.
       * Data: A count of all records in Entity_RotaryMember associated with the club.
   * Active Members:
       * UI: A card showing the number of active members and a percentage of the
         total.
       * Data: A count of Entity_RotaryMember where isActive is true.
   * Average Meeting Attendance:
       * UI: A card showing the average number of attendees for the last 5 meetings.
       * Data: Calculated from Entity_RotaryAttendance records linked to recent
         Entity_RotaryMeeting entries.

  B. Attendance Trend Chart (Left Column)

  A visual representation of member engagement over time.

   * UI: A simple line or bar chart.
   * Title: "Meeting Attendance - Last 30 Days"
   * Data:
       * X-Axis: Dates for the past 30 days.
       * Y-Axis: A count of Entity_RotaryAttendance records for each day.

  C. Recent Activity Table (Left Column)

  A live feed of important events within the club.

   * UI: A simple, scrollable table or list.
   * Title: "Recent Activity"
   * Data (Combined from entities):
       * "[Member Name] checked into [Meeting Title]" (from Entity_RotaryAttendance)
       * "[Member Name] joined the club" (from Entity_RotaryMember using joinDate)
       * "New meeting scheduled: [Meeting Title]" (from Entity_RotaryMeeting)

  D. Club Details Panel (Right Column)

  Static information providing context about the club being viewed.

   * UI: A clean, easy-to-read information panel.
   * Title: The clubName from Entity_Rotary.
   * Data:
       * District: district
       * Location: location
       * Charter Date: charterDate
       * Contact: contactEmail

  E. Upcoming Meetings List (Right Column)

  Allows users to quickly see what's next.

   * UI: A simple list with dates.
   * Title: "Upcoming Meetings"
   * Data: A list of future meetings from Entity_RotaryMeeting (meetingTitle and
     startTime).

  F. Quick Actions Section (Right Column)

  Buttons for the most common administrative tasks.

   * UI: A set of prominent buttons.
   * Actions:
       * "Add New Member"
       * "Schedule a Meeting"
       * "Record Attendance"

  ---

  This dashboard structure provides a solid foundation for the Rotary tenant's web
  portal, focusing on the most critical information and actions. The next step would
  be to create detailed wireframes and build the front-end components.


    vim.lsp.buf.format({ async = true })
  end,
})
