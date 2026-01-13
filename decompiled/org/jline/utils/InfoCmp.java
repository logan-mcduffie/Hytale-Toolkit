package org.jline.utils;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class InfoCmp {
   private static final Map<String, Object> CAPS_DEFAULT = new HashMap<>();
   private static final Map<String, Object> CAPS_LOADED = new HashMap<>();

   private InfoCmp() {
   }

   public static Map<String, InfoCmp.Capability> getCapabilitiesByName() {
      Map<String, InfoCmp.Capability> capabilities = new LinkedHashMap<>();

      try {
         InputStream is = InfoCmp.class.getResourceAsStream("capabilities.txt");

         Object var3;
         try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            try {
               br.lines().map(String::trim).filter(s -> !s.startsWith("#")).filter(s -> !s.isEmpty()).forEach(s -> {
                  String[] names = s.split(", ");
                  InfoCmp.Capability cap = Enum.valueOf(InfoCmp.Capability.class, names[0]);
                  capabilities.put(names[0], cap);
                  capabilities.put(names[1], cap);
               });
               var3 = capabilities;
            } catch (Throwable var7) {
               try {
                  br.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }

               throw var7;
            }

            br.close();
         } catch (Throwable var8) {
            if (is != null) {
               try {
                  is.close();
               } catch (Throwable var5) {
                  var8.addSuppressed(var5);
               }
            }

            throw var8;
         }

         if (is != null) {
            is.close();
         }

         return (Map<String, InfoCmp.Capability>)var3;
      } catch (IOException var9) {
         throw new IOError(var9);
      }
   }

   public static void setDefaultInfoCmp(String terminal, String caps) {
      CAPS_DEFAULT.putIfAbsent(terminal, caps);
   }

   public static void setDefaultInfoCmp(String terminal, Supplier<String> caps) {
      CAPS_DEFAULT.putIfAbsent(terminal, caps);
   }

   public static String getDefaultInfoCmp(String terminal) {
      Object caps = CAPS_DEFAULT.get(terminal);
      if (caps instanceof Supplier) {
         caps = ((Supplier)caps).get();
      }

      return (String)caps;
   }

   public static void setLoadedInfoCmp(String terminal, String caps) {
      CAPS_LOADED.put(terminal, caps);
   }

   public static void setLoadedInfoCmp(String terminal, Supplier<String> caps) {
      CAPS_LOADED.put(terminal, caps);
   }

   public static String getLoadedInfoCmp(String terminal) {
      Object caps = CAPS_LOADED.get(terminal);
      if (caps instanceof Supplier) {
         caps = ((Supplier)caps).get();
      }

      return (String)caps;
   }

   public static String getInfoCmp(String terminal) throws IOException, InterruptedException {
      IOException error = new IOException("Unable to retrieve infocmp for " + terminal);
      String caps = getLoadedInfoCmp(terminal);
      if (caps == null) {
         try {
            Process p = new ProcessBuilder(OSUtils.INFOCMP_COMMAND, "-x", terminal).start();
            caps = ExecHelper.waitAndCapture(p);
            if (p.exitValue() != 0) {
               error.addSuppressed(
                  new IOException(
                     "Command '" + OSUtils.INFOCMP_COMMAND + " -x " + terminal + "' failed with exit code " + p.exitValue() + " and output '" + caps + "'"
                  )
               );
               caps = null;
            }
         } catch (IOException var5) {
            error.addSuppressed(var5);
         }
      }

      if (caps == null) {
         try {
            Process p = new ProcessBuilder(OSUtils.INFOCMP_COMMAND, terminal).start();
            caps = ExecHelper.waitAndCapture(p);
            if (p.exitValue() != 0) {
               error.addSuppressed(
                  new IOException(
                     "Command '" + OSUtils.INFOCMP_COMMAND + " " + terminal + "' failed with exit code " + p.exitValue() + " and output '" + caps + "'"
                  )
               );
               caps = null;
            }
         } catch (IOException var4) {
            error.addSuppressed(var4);
         }
      }

      if (caps != null) {
         setLoadedInfoCmp(terminal, caps);
      } else {
         caps = getDefaultInfoCmp(terminal);
         if (caps == null) {
            throw error;
         }
      }

      return caps;
   }

   public static void parseInfoCmp(
      String capabilities, Set<InfoCmp.Capability> bools, Map<InfoCmp.Capability, Integer> ints, Map<InfoCmp.Capability, String> strings
   ) {
      Map<String, InfoCmp.Capability> capsByName = getCapabilitiesByName();
      String[] lines = capabilities.split("\n");

      for (int i = 1; i < lines.length; i++) {
         Matcher m = Pattern.compile("\\s*(([^,]|\\\\,)+)\\s*[,$]").matcher(lines[i]);

         while (m.find()) {
            String cap = m.group(1);
            if (cap.contains("#")) {
               int index = cap.indexOf(35);
               String key = cap.substring(0, index);
               String val = cap.substring(index + 1);
               int iVal;
               if ("0".equals(val)) {
                  iVal = 0;
               } else if (val.startsWith("0x")) {
                  iVal = Integer.parseInt(val.substring(2), 16);
               } else if (val.startsWith("0")) {
                  iVal = Integer.parseInt(val.substring(1), 8);
               } else {
                  iVal = Integer.parseInt(val);
               }

               InfoCmp.Capability c = capsByName.get(key);
               if (c != null) {
                  ints.put(c, iVal);
               }
            } else if (cap.contains("=")) {
               int indexx = cap.indexOf(61);
               String keyx = cap.substring(0, indexx);
               String valx = cap.substring(indexx + 1);
               InfoCmp.Capability c = capsByName.get(keyx);
               if (c != null) {
                  strings.put(c, valx);
               }
            } else {
               InfoCmp.Capability c = capsByName.get(cap);
               if (c != null) {
                  bools.add(c);
               }
            }
         }
      }
   }

   static String loadDefaultInfoCmp(String name) {
      try {
         InputStream is = InfoCmp.class.getResourceAsStream(name + ".caps");

         String var3;
         try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            try {
               var3 = br.lines().collect(Collectors.joining("\n", "", "\n"));
            } catch (Throwable var7) {
               try {
                  br.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }

               throw var7;
            }

            br.close();
         } catch (Throwable var8) {
            if (is != null) {
               try {
                  is.close();
               } catch (Throwable var5) {
                  var8.addSuppressed(var5);
               }
            }

            throw var8;
         }

         if (is != null) {
            is.close();
         }

         return var3;
      } catch (IOException var9) {
         throw new IOError(var9);
      }
   }

   static {
      for (String s : Arrays.asList(
         "dumb",
         "dumb-color",
         "ansi",
         "xterm",
         "xterm-256color",
         "windows",
         "windows-256color",
         "windows-conemu",
         "windows-vtp",
         "screen",
         "screen-256color",
         "rxvt-unicode",
         "rxvt-unicode-256color",
         "rxvt-basic",
         "rxvt"
      )) {
         setDefaultInfoCmp(s, () -> loadDefaultInfoCmp(s));
      }
   }

   public static enum Capability {
      auto_left_margin,
      auto_right_margin,
      back_color_erase,
      can_change,
      ceol_standout_glitch,
      col_addr_glitch,
      cpi_changes_res,
      cr_cancels_micro_mode,
      dest_tabs_magic_smso,
      eat_newline_glitch,
      erase_overstrike,
      generic_type,
      hard_copy,
      hard_cursor,
      has_meta_key,
      has_print_wheel,
      has_status_line,
      hue_lightness_saturation,
      insert_null_glitch,
      lpi_changes_res,
      memory_above,
      memory_below,
      move_insert_mode,
      move_standout_mode,
      needs_xon_xoff,
      no_esc_ctlc,
      no_pad_char,
      non_dest_scroll_region,
      non_rev_rmcup,
      over_strike,
      prtr_silent,
      row_addr_glitch,
      semi_auto_right_margin,
      status_line_esc_ok,
      tilde_glitch,
      transparent_underline,
      xon_xoff,
      columns,
      init_tabs,
      label_height,
      label_width,
      lines,
      lines_of_memory,
      magic_cookie_glitch,
      max_attributes,
      max_colors,
      max_pairs,
      maximum_windows,
      no_color_video,
      num_labels,
      padding_baud_rate,
      virtual_terminal,
      width_status_line,
      bit_image_entwining,
      bit_image_type,
      buffer_capacity,
      buttons,
      dot_horz_spacing,
      dot_vert_spacing,
      max_micro_address,
      max_micro_jump,
      micro_col_size,
      micro_line_size,
      number_of_pins,
      output_res_char,
      output_res_horz_inch,
      output_res_line,
      output_res_vert_inch,
      print_rate,
      wide_char_size,
      acs_chars,
      back_tab,
      bell,
      carriage_return,
      change_char_pitch,
      change_line_pitch,
      change_res_horz,
      change_res_vert,
      change_scroll_region,
      char_padding,
      clear_all_tabs,
      clear_margins,
      clear_screen,
      clr_bol,
      clr_eol,
      clr_eos,
      column_address,
      command_character,
      create_window,
      cursor_address,
      cursor_down,
      cursor_home,
      cursor_invisible,
      cursor_left,
      cursor_mem_address,
      cursor_normal,
      cursor_right,
      cursor_to_ll,
      cursor_up,
      cursor_visible,
      define_char,
      delete_character,
      delete_line,
      dial_phone,
      dis_status_line,
      display_clock,
      down_half_line,
      ena_acs,
      enter_alt_charset_mode,
      enter_am_mode,
      enter_blink_mode,
      enter_bold_mode,
      enter_ca_mode,
      enter_delete_mode,
      enter_dim_mode,
      enter_doublewide_mode,
      enter_draft_quality,
      enter_insert_mode,
      enter_italics_mode,
      enter_leftward_mode,
      enter_micro_mode,
      enter_near_letter_quality,
      enter_normal_quality,
      enter_protected_mode,
      enter_reverse_mode,
      enter_secure_mode,
      enter_shadow_mode,
      enter_standout_mode,
      enter_subscript_mode,
      enter_superscript_mode,
      enter_underline_mode,
      enter_upward_mode,
      enter_xon_mode,
      erase_chars,
      exit_alt_charset_mode,
      exit_am_mode,
      exit_attribute_mode,
      exit_ca_mode,
      exit_delete_mode,
      exit_doublewide_mode,
      exit_insert_mode,
      exit_italics_mode,
      exit_leftward_mode,
      exit_micro_mode,
      exit_shadow_mode,
      exit_standout_mode,
      exit_subscript_mode,
      exit_superscript_mode,
      exit_underline_mode,
      exit_upward_mode,
      exit_xon_mode,
      fixed_pause,
      flash_hook,
      flash_screen,
      form_feed,
      from_status_line,
      goto_window,
      hangup,
      init_1string,
      init_2string,
      init_3string,
      init_file,
      init_prog,
      initialize_color,
      initialize_pair,
      insert_character,
      insert_line,
      insert_padding,
      key_a1,
      key_a3,
      key_b2,
      key_backspace,
      key_beg,
      key_btab,
      key_c1,
      key_c3,
      key_cancel,
      key_catab,
      key_clear,
      key_close,
      key_command,
      key_copy,
      key_create,
      key_ctab,
      key_dc,
      key_dl,
      key_down,
      key_eic,
      key_end,
      key_enter,
      key_eol,
      key_eos,
      key_exit,
      key_f0,
      key_f1,
      key_f10,
      key_f11,
      key_f12,
      key_f13,
      key_f14,
      key_f15,
      key_f16,
      key_f17,
      key_f18,
      key_f19,
      key_f2,
      key_f20,
      key_f21,
      key_f22,
      key_f23,
      key_f24,
      key_f25,
      key_f26,
      key_f27,
      key_f28,
      key_f29,
      key_f3,
      key_f30,
      key_f31,
      key_f32,
      key_f33,
      key_f34,
      key_f35,
      key_f36,
      key_f37,
      key_f38,
      key_f39,
      key_f4,
      key_f40,
      key_f41,
      key_f42,
      key_f43,
      key_f44,
      key_f45,
      key_f46,
      key_f47,
      key_f48,
      key_f49,
      key_f5,
      key_f50,
      key_f51,
      key_f52,
      key_f53,
      key_f54,
      key_f55,
      key_f56,
      key_f57,
      key_f58,
      key_f59,
      key_f6,
      key_f60,
      key_f61,
      key_f62,
      key_f63,
      key_f7,
      key_f8,
      key_f9,
      key_find,
      key_help,
      key_home,
      key_ic,
      key_il,
      key_left,
      key_ll,
      key_mark,
      key_message,
      key_move,
      key_next,
      key_npage,
      key_open,
      key_options,
      key_ppage,
      key_previous,
      key_print,
      key_redo,
      key_reference,
      key_refresh,
      key_replace,
      key_restart,
      key_resume,
      key_right,
      key_save,
      key_sbeg,
      key_scancel,
      key_scommand,
      key_scopy,
      key_screate,
      key_sdc,
      key_sdl,
      key_select,
      key_send,
      key_seol,
      key_sexit,
      key_sf,
      key_sfind,
      key_shelp,
      key_shome,
      key_sic,
      key_sleft,
      key_smessage,
      key_smove,
      key_snext,
      key_soptions,
      key_sprevious,
      key_sprint,
      key_sr,
      key_sredo,
      key_sreplace,
      key_sright,
      key_srsume,
      key_ssave,
      key_ssuspend,
      key_stab,
      key_sundo,
      key_suspend,
      key_undo,
      key_up,
      keypad_local,
      keypad_xmit,
      lab_f0,
      lab_f1,
      lab_f10,
      lab_f2,
      lab_f3,
      lab_f4,
      lab_f5,
      lab_f6,
      lab_f7,
      lab_f8,
      lab_f9,
      label_format,
      label_off,
      label_on,
      meta_off,
      meta_on,
      micro_column_address,
      micro_down,
      micro_left,
      micro_right,
      micro_row_address,
      micro_up,
      newline,
      order_of_pins,
      orig_colors,
      orig_pair,
      pad_char,
      parm_dch,
      parm_delete_line,
      parm_down_cursor,
      parm_down_micro,
      parm_ich,
      parm_index,
      parm_insert_line,
      parm_left_cursor,
      parm_left_micro,
      parm_right_cursor,
      parm_right_micro,
      parm_rindex,
      parm_up_cursor,
      parm_up_micro,
      pkey_key,
      pkey_local,
      pkey_xmit,
      plab_norm,
      print_screen,
      prtr_non,
      prtr_off,
      prtr_on,
      pulse,
      quick_dial,
      remove_clock,
      repeat_char,
      req_for_input,
      reset_1string,
      reset_2string,
      reset_3string,
      reset_file,
      restore_cursor,
      row_address,
      save_cursor,
      scroll_forward,
      scroll_reverse,
      select_char_set,
      set_attributes,
      set_background,
      set_bottom_margin,
      set_bottom_margin_parm,
      set_clock,
      set_color_pair,
      set_foreground,
      set_left_margin,
      set_left_margin_parm,
      set_right_margin,
      set_right_margin_parm,
      set_tab,
      set_top_margin,
      set_top_margin_parm,
      set_window,
      start_bit_image,
      start_char_set_def,
      stop_bit_image,
      stop_char_set_def,
      subscript_characters,
      superscript_characters,
      tab,
      these_cause_cr,
      to_status_line,
      tone,
      underline_char,
      up_half_line,
      user0,
      user1,
      user2,
      user3,
      user4,
      user5,
      user6,
      user7,
      user8,
      user9,
      wait_tone,
      xoff_character,
      xon_character,
      zero_motion,
      alt_scancode_esc,
      bit_image_carriage_return,
      bit_image_newline,
      bit_image_repeat,
      char_set_names,
      code_set_init,
      color_names,
      define_bit_image_region,
      device_type,
      display_pc_char,
      end_bit_image_region,
      enter_pc_charset_mode,
      enter_scancode_mode,
      exit_pc_charset_mode,
      exit_scancode_mode,
      get_mouse,
      key_mouse,
      mouse_info,
      pc_term_options,
      pkey_plab,
      req_mouse_pos,
      scancode_escape,
      set0_des_seq,
      set1_des_seq,
      set2_des_seq,
      set3_des_seq,
      set_a_background,
      set_a_foreground,
      set_color_band,
      set_lr_margin,
      set_page_length,
      set_tb_margin,
      enter_horizontal_hl_mode,
      enter_left_hl_mode,
      enter_low_hl_mode,
      enter_right_hl_mode,
      enter_top_hl_mode,
      enter_vertical_hl_mode,
      set_a_attributes,
      set_pglen_inch,
      xm,
      XM;

      public String[] getNames() {
         return InfoCmp.getCapabilitiesByName().entrySet().stream().filter(e -> e.getValue() == this).map(Entry::getKey).toArray(String[]::new);
      }

      public static InfoCmp.Capability byName(String name) {
         return InfoCmp.getCapabilitiesByName().get(name);
      }
   }
}
