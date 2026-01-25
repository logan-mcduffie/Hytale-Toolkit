#!/usr/bin/env python3
"""
Hytale Toolkit - Installation Wizard (PyQt6)
A graphical setup wizard with proper translucency support.
"""

from pathlib import Path as _Path

# Read version from VERSION file
# When running as PyInstaller bundle, check sys._MEIPASS first
import sys as _sys
if getattr(_sys, '_MEIPASS', None):
    _version_file = _Path(_sys._MEIPASS) / "VERSION"
else:
    _version_file = _Path(__file__).parent.parent / "VERSION"
__version__ = _version_file.read_text().strip() if _version_file.exists() else "0.0.0"

import json
import os
import re
import subprocess
import sys
import shutil
import urllib.request
import webbrowser
from datetime import datetime
from pathlib import Path
from PyQt6.QtWidgets import (
    QApplication,
    QMainWindow,
    QWidget,
    QVBoxLayout,
    QHBoxLayout,
    QGridLayout,
    QLabel,
    QPushButton,
    QFrame,
    QStackedWidget,
    QFileDialog,
    QLineEdit,
    QCheckBox,
    QSlider,
    QScrollArea,
    QProgressBar,
    QSizePolicy,
    QPlainTextEdit,
    QMessageBox,
    QDialog,
    QDialogButtonBox,
)
from PyQt6.QtCore import Qt, QSize, QProcess, pyqtSignal, QTimer
from PyQt6.QtGui import (
    QPixmap,
    QPainter,
    QColor,
    QFont,
    QFontDatabase,
    QPalette,
    QBrush,
    QTextCursor,
    QIcon,
)
from PyQt6.QtWidgets import QGraphicsOpacityEffect

# Import setup functions for MCP configuration
# NOTE: When running as PyInstaller bundle, setup.py is not bundled - it exists
# in the user's installed toolkit. MCP configuration must be done by running
# from the installed toolkit directory, not the standalone installer exe.
if getattr(_sys, '_MEIPASS', None):
    # Running as bundled exe - setup module not available
    SETUP_AVAILABLE = False
else:
    SCRIPT_DIR = Path(__file__).parent.resolve()
    sys.path.insert(0, str(SCRIPT_DIR))
    try:
        from setup import (
            setup_claude_code,
            setup_vscode,
            setup_cursor,
            setup_windsurf,
            setup_codex,
            setup_jetbrains,
            create_start_scripts,
            get_mcp_command_stdio,
        )
        SETUP_AVAILABLE = True
    except ImportError:
        SETUP_AVAILABLE = False


def get_base_path() -> Path:
    """Get the base path for resources."""
    if getattr(_sys, '_MEIPASS', None):
        return Path(_sys._MEIPASS)
    else:
        return Path(__file__).parent.resolve()


def get_icon_path(name: str) -> Path | None:
    """Get path to an icon file if it exists."""
    icons_dir = get_base_path() / "assets" / "icons"
    for ext in [".png", ".svg", ".ico"]:
        path = icons_dir / f"{name}{ext}"
        if path.exists():
            return path
    return None


# =============================================================================
# Update Checker
# =============================================================================

GITHUB_REPO = "logan-mcduffie/Hytale-Toolkit"
RELEASES_URL = f"https://api.github.com/repos/{GITHUB_REPO}/releases/latest"
RELEASES_PAGE = f"https://github.com/{GITHUB_REPO}/releases/latest"


def compare_versions(current: str, latest: str) -> int:
    """
    Compare two semver versions.
    Returns: -1 if current < latest, 0 if equal, 1 if current > latest
    """
    def parse(v: str) -> tuple:
        # Remove 'v' prefix if present
        v = v.lstrip('v')
        # Split and convert to integers
        parts = v.split('.')
        return tuple(int(p) for p in parts[:3])

    try:
        current_parts = parse(current)
        latest_parts = parse(latest)

        if current_parts < latest_parts:
            return -1
        elif current_parts > latest_parts:
            return 1
        return 0
    except (ValueError, IndexError):
        return 0  # Can't compare, assume equal


def check_for_updates(current_version: str) -> dict | None:
    """
    Check GitHub for the latest release.
    Returns dict with 'version', 'url', 'notes' if update available, None otherwise.
    """
    try:
        req = urllib.request.Request(
            RELEASES_URL,
            headers={"Accept": "application/vnd.github.v3+json", "User-Agent": "Hytale-Toolkit"}
        )
        with urllib.request.urlopen(req, timeout=5) as response:
            data = json.loads(response.read().decode())

        latest_version = data.get("tag_name", "").lstrip('v')
        if not latest_version:
            return None

        if compare_versions(current_version, latest_version) < 0:
            return {
                "version": latest_version,
                "url": data.get("html_url", RELEASES_PAGE),
                "notes": data.get("body", ""),
            }
    except Exception:
        pass  # Network error, timeout, etc. - silently fail

    return None


class UpdateDialog(QDialog):
    """Dialog shown when an update is available."""

    def __init__(self, current_version: str, update_info: dict, parent=None):
        super().__init__(parent)
        self.update_info = update_info
        self.setWindowTitle("Update Available")
        self.setFixedSize(450, 300)

        # Set window icon (check PyInstaller bundle first)
        if getattr(_sys, '_MEIPASS', None):
            icon_path = _Path(_sys._MEIPASS) / ".github" / "logo-transparent.png"
        else:
            icon_path = _Path(__file__).parent.parent / ".github" / "logo-transparent.png"
        if icon_path.exists():
            from PyQt6.QtGui import QIcon
            self.setWindowIcon(QIcon(str(icon_path)))

        self.setStyleSheet("""
            QDialog {
                background-color: #1e1e1e;
            }
            QLabel {
                color: white;
            }
            QPushButton {
                background-color: #3498db;
                color: white;
                border: none;
                padding: 10px 20px;
                border-radius: 5px;
                font-weight: bold;
            }
            QPushButton:hover {
                background-color: #2980b9;
            }
            QPushButton#skipButton {
                background-color: #444444;
            }
            QPushButton#skipButton:hover {
                background-color: #555555;
            }
        """)

        layout = QVBoxLayout(self)
        layout.setSpacing(15)
        layout.setContentsMargins(25, 25, 25, 25)

        # Title
        title = QLabel("A new version is available!")
        title.setStyleSheet("font-size: 18px; font-weight: bold; color: #22C55E; background: transparent;")
        title.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(title)

        # Version info
        version_text = f"Current: v{current_version}  →  Latest: v{update_info['version']}"
        version_label = QLabel(version_text)
        version_label.setStyleSheet("font-size: 14px; color: #aaaaaa; background: transparent;")
        version_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(version_label)

        # Release notes in scrollable area
        notes = update_info.get("notes", "")
        if notes:
            from PyQt6.QtWidgets import QScrollArea

            notes_label = QLabel(notes.strip())
            notes_label.setStyleSheet("font-size: 11px; color: #888888; background: transparent;")
            notes_label.setWordWrap(True)

            scroll_area = QScrollArea()
            scroll_area.setWidget(notes_label)
            scroll_area.setWidgetResizable(True)
            scroll_area.setStyleSheet("""
                QScrollArea {
                    border: none;
                    background: transparent;
                }
                QScrollArea > QWidget > QWidget {
                    background: transparent;
                }
                QScrollBar:vertical {
                    background: #2a2a2a;
                    width: 8px;
                    border-radius: 4px;
                }
                QScrollBar::handle:vertical {
                    background: #555555;
                    border-radius: 4px;
                }
                QScrollBar::add-line:vertical, QScrollBar::sub-line:vertical {
                    height: 0px;
                }
            """)
            scroll_area.setMaximumHeight(140)
            layout.addWidget(scroll_area)

        layout.addStretch()

        # Buttons
        button_layout = QHBoxLayout()
        button_layout.setSpacing(10)

        skip_btn = QPushButton("Skip")
        skip_btn.setObjectName("skipButton")
        skip_btn.clicked.connect(self.reject)
        button_layout.addWidget(skip_btn)

        button_layout.addStretch()

        download_btn = QPushButton("Download Update")
        download_btn.clicked.connect(self._open_download)
        button_layout.addWidget(download_btn)

        layout.addLayout(button_layout)

    def _open_download(self):
        """Open the release page in browser."""
        webbrowser.open(self.update_info["url"])
        self.accept()


def fix_decompiled_file(filepath: Path) -> bool:
    """
    Fix a single Java file with decompilation artifacts.
    Returns True if modifications were made.
    """
    try:
        content = filepath.read_text(encoding='utf-8', errors='replace')
    except Exception:
        return False

    original = content

    # Replace <unrepresentable> with a valid Java identifier
    content = content.replace('<unrepresentable>', 'DecompilerPlaceholder')

    # Replace $assertionsDisabled references with false
    content = re.sub(r'DecompilerPlaceholder\.\$assertionsDisabled', 'false', content)

    # Fix interfaces with static initializer blocks (not valid Java)
    if re.search(r'^public\s+interface\s+\w+', content, re.MULTILINE):
        # Find CODEC field declaration without initialization
        codec_match = re.search(r'(BuilderCodecMapCodec<[^>]+>)\s+CODEC\s*;', content)
        if codec_match:
            # Find the initialization in the static block
            init_match = re.search(r'CODEC\s*=\s*(new\s+BuilderCodecMapCodec<>\([^)]*\))\s*;', content)
            if init_match:
                # Replace uninitialized field with initialized one
                content = re.sub(
                    r'(BuilderCodecMapCodec<[^>]+>)\s+CODEC\s*;',
                    f'\\1 CODEC = {init_match.group(1)};',
                    content
                )

        # Remove static blocks from interfaces (they're not allowed)
        lines = content.split('\n')
        new_lines = []
        in_static_block = False
        brace_count = 0

        for line in lines:
            stripped = line.strip()
            if stripped.startswith('static {') or stripped == 'static {':
                in_static_block = True
                brace_count = line.count('{') - line.count('}')
                continue

            if in_static_block:
                brace_count += line.count('{') - line.count('}')
                if brace_count <= 0:
                    in_static_block = False
                continue

            new_lines.append(line)

        content = '\n'.join(new_lines)

    if content != original:
        try:
            filepath.write_text(content, encoding='utf-8')
            return True
        except Exception:
            return False

    return False


def fix_decompiled_files(decompiled_dir: Path, terminal=None) -> int:
    """
    Fix decompilation artifacts in all Java files.
    Returns the number of files fixed.
    """
    if not decompiled_dir.exists():
        return 0

    if terminal:
        terminal.append_info("Fixing decompilation artifacts...")

    count = 0
    java_files = list(decompiled_dir.rglob("*.java"))
    total = len(java_files)

    if terminal:
        terminal.append_line(f"  Found {total} Java files to process...")

    for i, filepath in enumerate(java_files):
        if fix_decompiled_file(filepath):
            count += 1

        # Process events every 100 files to keep UI responsive
        if i % 100 == 0:
            QApplication.processEvents()

    if terminal:
        terminal.append_success(f"  Fixed {count} files with decompilation artifacts")

    return count


class ToggleSwitch(QWidget):
    """A modern toggle switch widget."""

    def __init__(self, checked: bool = False, parent=None):
        super().__init__(parent)
        self._checked = checked
        self._callbacks = []
        self.setFixedSize(48, 26)
        self.setCursor(Qt.CursorShape.PointingHandCursor)

    def isChecked(self) -> bool:
        return self._checked

    def setChecked(self, checked: bool):
        if self._checked != checked:
            self._checked = checked
            self.update()
            for callback in self._callbacks:
                callback(checked)

    def connect_toggled(self, callback):
        """Connect a callback to the toggled event."""
        self._callbacks.append(callback)

    def mousePressEvent(self, _event):
        self.setChecked(not self._checked)

    def paintEvent(self, _event):
        painter = QPainter(self)
        painter.setRenderHint(QPainter.RenderHint.Antialiasing)

        # Track dimensions
        track_height = 26
        track_width = 48
        track_radius = track_height // 2

        # Draw track
        if self._checked:
            track_color = QColor("#22C55E")
        else:
            track_color = QColor("#3a3a3a")

        painter.setPen(Qt.PenStyle.NoPen)
        painter.setBrush(QBrush(track_color))
        painter.drawRoundedRect(0, 0, track_width, track_height, track_radius, track_radius)

        # Draw thumb (circle)
        thumb_diameter = 20
        thumb_margin = 3
        if self._checked:
            thumb_x = track_width - thumb_diameter - thumb_margin
        else:
            thumb_x = thumb_margin
        thumb_y = (track_height - thumb_diameter) // 2

        painter.setBrush(QBrush(QColor("white")))
        painter.drawEllipse(thumb_x, thumb_y, thumb_diameter, thumb_diameter)


class TerminalWidget(QPlainTextEdit):
    """A terminal-like widget for displaying command output."""

    def __init__(self, parent=None):
        super().__init__(parent)
        self.setReadOnly(True)
        self.setLineWrapMode(QPlainTextEdit.LineWrapMode.NoWrap)
        self.setStyleSheet("""
            QPlainTextEdit {
                background-color: #0d0d0d;
                color: #cccccc;
                border: 1px solid #333333;
                border-radius: 6px;
                padding: 10px;
                font-family: 'Cascadia Code', 'Consolas', 'Courier New', monospace;
                font-size: 11px;
            }
            QScrollBar:vertical {
                background: #1a1a1a;
                width: 10px;
                border-radius: 5px;
            }
            QScrollBar::handle:vertical {
                background: #444444;
                border-radius: 5px;
                min-height: 20px;
            }
            QScrollBar::handle:vertical:hover {
                background: #555555;
            }
            QScrollBar::add-line:vertical, QScrollBar::sub-line:vertical {
                height: 0px;
            }
        """)
        self._log_lines = []

    def append_line(self, text: str, color: str = None):
        """Append a line of text to the terminal."""
        self._log_lines.append(text)
        cursor = self.textCursor()
        cursor.movePosition(QTextCursor.MoveOperation.End)
        if color:
            self.appendHtml(f'<span style="color: {color};">{text}</span>')
        else:
            self.appendPlainText(text)
        # Auto-scroll to bottom
        self.verticalScrollBar().setValue(self.verticalScrollBar().maximum())

    def append_info(self, text: str):
        """Append an info message."""
        self.append_line(text, "#3498db")

    def append_success(self, text: str):
        """Append a success message."""
        self.append_line(text, "#22C55E")

    def append_error(self, text: str):
        """Append an error message."""
        self.append_line(text, "#EF4444")

    def append_warning(self, text: str):
        """Append a warning message."""
        self.append_line(text, "#F59E0B")

    def get_full_log(self) -> str:
        """Get the complete log as a string."""
        return "\n".join(self._log_lines)

    def clear_terminal(self):
        """Clear the terminal and log."""
        self.clear()
        self._log_lines = []


BUNDLE_DIR = get_base_path()


def load_env_api_key(toolkit_path: str = None) -> str:
    """Load Voyage API key from .env file if it exists.

    Checks the hytale-rag subdirectory where scripts expect the .env file.
    """
    if not toolkit_path:
        return ""

    # .env is in hytale-rag/ subdirectory
    env_path = Path(toolkit_path) / "hytale-rag" / ".env"
    if env_path.exists():
        try:
            with open(env_path, "r", encoding="utf-8") as f:
                for line in f:
                    line = line.strip()
                    if line.startswith("VOYAGE_API_KEY="):
                        # Extract value, handling quotes
                        value = line.split("=", 1)[1].strip()
                        # Remove surrounding quotes if present
                        if (value.startswith('"') and value.endswith('"')) or \
                           (value.startswith("'") and value.endswith("'")):
                            value = value[1:-1]
                        return value
        except Exception:
            pass
    return ""


# Colors
ACCENT_COLOR = "#1f6aa5"
SUCCESS_COLOR = "#22C55E"
ERROR_COLOR = "#EF4444"
WARNING_COLOR = "#F59E0B"
PANEL_COLOR = QColor(25, 25, 25, 220)  # Semi-transparent dark grey


class SidebarWidget(QWidget):
    """Sidebar with background image and translucent overlay."""

    def __init__(self, parent=None):
        super().__init__(parent)
        self.setFixedWidth(180)
        self.background_pixmap = None
        self.current_step = 0
        self.steps = [
            "Welcome",
            "Paths",
            "Decompile",
            "Javadocs",
            "Embedder",
            "Database",
            "Integration",
            "CLI Tools",
            "Complete",
        ]

        # Load background image
        bg_path = BUNDLE_DIR / "assets" / "sidebar_bg.jpg"
        if bg_path.exists():
            self.background_pixmap = QPixmap(str(bg_path))

    def set_step(self, step: int):
        """Update current step and repaint."""
        self.current_step = step
        self.update()

    def paintEvent(self, a0):
        """Custom paint for background image with translucent overlay."""
        painter = QPainter(self)
        painter.setRenderHint(QPainter.RenderHint.Antialiasing)

        rect = self.rect()

        # Draw background image scaled to fill height
        if self.background_pixmap:
            # Scale to fill height, crop width
            scaled = self.background_pixmap.scaledToHeight(
                rect.height(), Qt.TransformationMode.SmoothTransformation
            )
            # Center crop if wider than sidebar
            x_offset = (scaled.width() - rect.width()) // 2
            cropped = scaled.copy(x_offset, 0, rect.width(), rect.height())
            painter.drawPixmap(0, 0, cropped)
        else:
            # Fallback solid color
            painter.fillRect(rect, QColor("#1a1a1a"))

        # Draw translucent panel overlay (from y=45 to y=420)
        panel_rect = rect.adjusted(0, 45, 0, -(rect.height() - 420))
        painter.fillRect(panel_rect, PANEL_COLOR)

        # Draw logo text
        painter.setPen(QColor(ACCENT_COLOR))
        font = QFont("Segoe UI", 18)
        font.setBold(True)
        painter.setFont(font)
        painter.drawText(
            0, 55, rect.width(), 30, Qt.AlignmentFlag.AlignCenter, "Hytale"
        )

        painter.setPen(QColor("white"))
        painter.drawText(
            0, 87, rect.width(), 30, Qt.AlignmentFlag.AlignCenter, "Toolkit"
        )

        # Draw separator line
        painter.setPen(QColor("#444444"))
        painter.drawLine(15, 127, rect.width() - 15, 127)

        # Draw step indicators
        font = QFont("Segoe UI", 12)
        y_start = 142
        line_height = 30

        # Symbol font for checkmarks
        symbol_font = QFont("Segoe UI Symbol", 12)

        for i, step_name in enumerate(self.steps):
            y = y_start + i * line_height

            if i < self.current_step:
                # Completed - green checkmark (use symbol font for smooth rendering)
                painter.setPen(QColor(SUCCESS_COLOR))
                symbol_font.setBold(False)
                painter.setFont(symbol_font)
                painter.drawText(
                    15,
                    y,
                    25,
                    line_height,
                    Qt.AlignmentFlag.AlignRight | Qt.AlignmentFlag.AlignVCenter,
                    "\u2714",
                )
                font.setBold(False)
                painter.setFont(font)
                painter.drawText(
                    45,
                    y,
                    rect.width() - 55,
                    line_height,
                    Qt.AlignmentFlag.AlignLeft | Qt.AlignmentFlag.AlignVCenter,
                    step_name,
                )
            elif i == self.current_step:
                # Current - bold white with accent number
                painter.setPen(QColor(ACCENT_COLOR))
                font.setBold(False)
                painter.setFont(font)
                painter.drawText(
                    15,
                    y,
                    25,
                    line_height,
                    Qt.AlignmentFlag.AlignRight | Qt.AlignmentFlag.AlignVCenter,
                    f"{i + 1}.",
                )
                painter.setPen(QColor("white"))
                font.setBold(True)
                painter.setFont(font)
                painter.drawText(
                    45,
                    y,
                    rect.width() - 55,
                    line_height,
                    Qt.AlignmentFlag.AlignLeft | Qt.AlignmentFlag.AlignVCenter,
                    step_name,
                )
            else:
                # Future - gray
                painter.setPen(QColor("gray"))
                font.setBold(False)
                painter.setFont(font)
                painter.drawText(
                    15,
                    y,
                    25,
                    line_height,
                    Qt.AlignmentFlag.AlignRight | Qt.AlignmentFlag.AlignVCenter,
                    f"{i + 1}.",
                )
                painter.drawText(
                    45,
                    y,
                    rect.width() - 55,
                    line_height,
                    Qt.AlignmentFlag.AlignLeft | Qt.AlignmentFlag.AlignVCenter,
                    step_name,
                )


class WelcomePage(QWidget):
    """Welcome page with feature cards."""

    def __init__(self, parent=None):
        super().__init__(parent)
        layout = QVBoxLayout(self)
        layout.setContentsMargins(40, 30, 40, 30)

        # Welcome text
        welcome = QLabel("Welcome to the")
        welcome.setStyleSheet("color: #aaaaaa; font-size: 14px;")
        welcome.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(welcome)

        # Title
        title = QLabel("HYTALE TOOLKIT")
        title.setStyleSheet("font-size: 28px; font-weight: bold; color: #3498db;")
        title.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(title)

        # Tagline
        tagline = QLabel("Your gateway to Hytale modding")
        tagline.setStyleSheet("color: #aaaaaa; font-size: 13px;")
        tagline.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(tagline)

        layout.addSpacing(20)

        # Feature cards grid
        cards_widget = QWidget()
        cards_layout = QHBoxLayout(cards_widget)
        cards_layout.setSpacing(15)

        # Two columns
        left_col = QVBoxLayout()
        right_col = QVBoxLayout()

        features = [
            ("\u2699", "Decompiler", "Extract and browse\nHytale source code"),
            ("\U0001f4da", "Documentation", "Javadoc and\nsearchable references"),
            ("\U0001f50d", "Code Search", "Semantic search\nacross the codebase"),
            ("\U0001f916", "AI Assistant", "Works with any\nLLM provider"),
        ]

        for i, (icon, name, desc) in enumerate(features):
            card = self.create_card(icon, name, desc)
            if i % 2 == 0:
                left_col.addWidget(card)
            else:
                right_col.addWidget(card)

        cards_layout.addLayout(left_col)
        cards_layout.addLayout(right_col)
        layout.addWidget(cards_widget)

        layout.addStretch()

    def create_card(self, icon: str, title: str, description: str) -> QFrame:
        """Create a feature card with icon."""
        card = QFrame()
        card.setFixedSize(200, 130)
        card.setStyleSheet("""
            QFrame {
                background-color: #2b2b2b;
                border: 1px solid #3a3a3a;
                border-radius: 8px;
            }
            QFrame:hover {
                border-color: #3498db;
            }
            QLabel {
                background: transparent;
                border: none;
            }
        """)

        layout = QVBoxLayout(card)
        layout.setContentsMargins(15, 15, 15, 15)
        layout.setSpacing(5)
        layout.setAlignment(Qt.AlignmentFlag.AlignCenter)

        # Icon
        icon_label = QLabel(icon)
        icon_label.setStyleSheet(
            "font-family: 'Segoe UI Emoji'; font-size: 26px; color: #3498db;"
        )
        icon_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(icon_label)

        # Title
        title_label = QLabel(title)
        title_label.setStyleSheet(
            "font-family: 'Segoe UI'; font-size: 14px; font-weight: bold; color: white;"
        )
        title_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(title_label)

        # Description
        desc_label = QLabel(description)
        desc_label.setStyleSheet(
            "font-family: 'Segoe UI'; font-size: 11px; color: #999999;"
        )
        desc_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        desc_label.setWordWrap(True)
        layout.addWidget(desc_label)

        return card


class HytalePathPage(QWidget):
    """Page for selecting Hytale installation and toolkit data paths."""

    # Signals for state changes
    state_changed = pyqtSignal(str)  # idle, downloading, completed

    def __init__(self, parent=None):
        super().__init__(parent)
        self.hytale_path = None
        self.toolkit_path = None
        self._button_callback = None
        self._back_button_callback = None
        self._state = "idle"  # idle, downloading, completed
        self._process = None
        self._toolkit_downloaded = False
        self._dot_count = 0
        self._dot_timer = None
        self._last_error = ""

        # Default toolkit path
        if sys.platform == "win32":
            default_toolkit = Path(os.environ.get("LOCALAPPDATA", "")) / "Hytale-Toolkit"
        else:
            default_toolkit = Path.home() / ".hytale-toolkit"

        layout = QVBoxLayout(self)
        layout.setContentsMargins(40, 30, 40, 20)

        # Title
        title = QLabel("Configure Paths")
        title.setStyleSheet("font-size: 22px; font-weight: bold; color: white;")
        title.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(title)

        layout.addSpacing(20)

        # ===== Hytale Game Location Section =====
        hytale_section = QWidget()
        hytale_layout = QVBoxLayout(hytale_section)
        hytale_layout.setContentsMargins(0, 0, 0, 0)
        hytale_layout.setSpacing(8)

        hytale_header = QLabel("Hytale Game Location")
        hytale_header.setStyleSheet("font-size: 14px; font-weight: bold; color: white;")
        hytale_layout.addWidget(hytale_header)

        hytale_hint = QLabel("The 'latest' folder containing Client/, Server/, and Assets.zip")
        hytale_hint.setStyleSheet("font-size: 11px; color: #888888;")
        hytale_layout.addWidget(hytale_hint)

        # Path input row
        hytale_row = QWidget()
        hytale_row_layout = QHBoxLayout(hytale_row)
        hytale_row_layout.setContentsMargins(0, 0, 0, 0)
        hytale_row_layout.setSpacing(8)

        self.hytale_input = QLineEdit()
        self.hytale_input.setPlaceholderText("Select Hytale installation path...")
        self.hytale_input.setStyleSheet("""
            QLineEdit {
                background-color: #1e1e1e;
                border: 1px solid #3a3a3a;
                border-radius: 6px;
                padding: 10px 12px;
                font-size: 12px;
                color: white;
            }
            QLineEdit:focus {
                border-color: #3498db;
            }
        """)
        self.hytale_input.textChanged.connect(self.validate_hytale_path)
        hytale_row_layout.addWidget(self.hytale_input)

        hytale_browse = QPushButton("Browse")
        hytale_browse.setStyleSheet("""
            QPushButton {
                background-color: #1f6aa5;
                padding: 10px 16px;
                border-radius: 6px;
                font-size: 12px;
            }
            QPushButton:hover {
                background-color: #2980b9;
            }
        """)
        hytale_browse.clicked.connect(self.browse_hytale)
        hytale_row_layout.addWidget(hytale_browse)

        hytale_layout.addWidget(hytale_row)

        # Hytale validation status
        self.hytale_status = QLabel("")
        self.hytale_status.setStyleSheet("font-size: 12px;")
        hytale_layout.addWidget(self.hytale_status)

        # Validation checklist (hidden by default)
        self.checklist_card = QFrame()
        self.checklist_card.setStyleSheet("QFrame { background-color: transparent; border: none; }")
        self.checklist_card.hide()

        checklist_layout = QVBoxLayout(self.checklist_card)
        checklist_layout.setContentsMargins(0, 0, 0, 0)
        checklist_layout.setSpacing(4)

        self.client_check = QLabel("")
        self.server_check = QLabel("")
        self.assets_check = QLabel("")

        for check in [self.client_check, self.server_check, self.assets_check]:
            check.setStyleSheet("font-size: 12px; color: #666666;")
            checklist_layout.addWidget(check)

        hytale_layout.addWidget(self.checklist_card)
        layout.addWidget(hytale_section)

        # Separator
        separator = QFrame()
        separator.setFrameShape(QFrame.Shape.HLine)
        separator.setStyleSheet("background-color: #3a3a3a;")
        separator.setFixedHeight(1)
        layout.addSpacing(15)
        layout.addWidget(separator)
        layout.addSpacing(15)

        # ===== Toolkit Data Location Section =====
        toolkit_section = QWidget()
        toolkit_layout = QVBoxLayout(toolkit_section)
        toolkit_layout.setContentsMargins(0, 0, 0, 0)
        toolkit_layout.setSpacing(8)

        toolkit_header = QLabel("Toolkit Data Location")
        toolkit_header.setStyleSheet("font-size: 14px; font-weight: bold; color: white;")
        toolkit_layout.addWidget(toolkit_header)

        toolkit_hint = QLabel("Where decompiled code, databases, and generated files will be stored")
        toolkit_hint.setStyleSheet("font-size: 11px; color: #888888;")
        toolkit_layout.addWidget(toolkit_hint)

        # Toolkit path input row
        toolkit_row = QWidget()
        toolkit_row_layout = QHBoxLayout(toolkit_row)
        toolkit_row_layout.setContentsMargins(0, 0, 0, 0)
        toolkit_row_layout.setSpacing(8)

        self.toolkit_input = QLineEdit()
        self.toolkit_input.setText(str(default_toolkit))
        self.toolkit_input.setStyleSheet("""
            QLineEdit {
                background-color: #1e1e1e;
                border: 1px solid #3a3a3a;
                border-radius: 6px;
                padding: 10px 12px;
                font-size: 12px;
                color: white;
            }
            QLineEdit:focus {
                border-color: #3498db;
            }
        """)
        self.toolkit_input.textChanged.connect(self.validate_toolkit_path)
        toolkit_row_layout.addWidget(self.toolkit_input)

        toolkit_browse = QPushButton("Browse")
        toolkit_browse.setStyleSheet("""
            QPushButton {
                background-color: #1f6aa5;
                padding: 10px 16px;
                border-radius: 6px;
                font-size: 12px;
            }
            QPushButton:hover {
                background-color: #2980b9;
            }
        """)
        toolkit_browse.clicked.connect(self.browse_toolkit)
        toolkit_row_layout.addWidget(toolkit_browse)

        toolkit_layout.addWidget(toolkit_row)

        # Toolkit validation status
        self.toolkit_status = QLabel("")
        self.toolkit_status.setStyleSheet("font-size: 12px;")
        toolkit_layout.addWidget(self.toolkit_status)

        # Download status (hidden by default)
        self.download_status = QLabel("")
        self.download_status.setStyleSheet("font-size: 12px; color: #3498db;")
        self.download_status.setOpenExternalLinks(True)  # Make links clickable
        self.download_status.hide()
        toolkit_layout.addWidget(self.download_status)

        layout.addWidget(toolkit_section)

        layout.addStretch()

        # Validate default toolkit path on init
        self.validate_toolkit_path()

        # Try to auto-detect Hytale installation
        self._auto_detect_hytale()

    def _auto_detect_hytale(self):
        """Try to find Hytale installation in common locations."""
        common_paths = []

        if sys.platform == "win32":
            # Windows common paths - Hytale Launcher paths first
            common_paths = [
                # Hytale Launcher default install locations
                Path("D:/Roaming/install/release/package/game/latest"),
                Path("C:/Roaming/install/release/package/game/latest"),
                Path("E:/Roaming/install/release/package/game/latest"),
                # Other common locations
                Path(os.environ.get("LOCALAPPDATA", "")) / "Programs" / "Hytale" / "game" / "latest",
                Path("C:/Program Files/Hytale/game/latest"),
                Path("C:/Program Files (x86)/Hytale/game/latest"),
                Path(os.environ.get("APPDATA", "")) / "Hytale" / "game" / "latest",
                Path.home() / "Hytale" / "game" / "latest",
            ]
            # Check common Steam library locations
            steam_paths = [
                Path("C:/Program Files (x86)/Steam/steamapps/common/Hytale/game/latest"),
                Path("D:/Steam/steamapps/common/Hytale/game/latest"),
                Path("D:/SteamLibrary/steamapps/common/Hytale/game/latest"),
                Path("E:/SteamLibrary/steamapps/common/Hytale/game/latest"),
            ]
            common_paths.extend(steam_paths)
        elif sys.platform == "darwin":
            # macOS common paths
            common_paths = [
                Path("/Applications/Hytale/game/latest"),
                Path.home() / "Library" / "Application Support" / "Hytale" / "game" / "latest",
                Path.home() / "Applications" / "Hytale" / "game" / "latest",
            ]
        else:
            # Linux common paths
            common_paths = [
                Path.home() / ".local" / "share" / "Hytale" / "game" / "latest",
                Path.home() / "Hytale" / "game" / "latest",
                Path.home() / ".steam" / "steam" / "steamapps" / "common" / "Hytale" / "game" / "latest",
            ]

        # Check each path for valid Hytale installation
        for path in common_paths:
            if path.exists():
                # Check if it has the expected structure
                client_dir = path / "Client"
                server_dir = path / "Server"
                assets_file = path / "Assets.zip"

                if client_dir.exists() or server_dir.exists() or assets_file.exists():
                    self.hytale_input.setText(str(path))
                    return

    def browse_hytale(self):
        """Open folder browser for Hytale path."""
        folder = QFileDialog.getExistingDirectory(
            self,
            "Select Hytale Installation Folder",
            "",
            QFileDialog.Option.ShowDirsOnly,
        )
        if folder:
            self.hytale_input.setText(folder)

    def browse_toolkit(self):
        """Open folder browser for toolkit data path."""
        folder = QFileDialog.getExistingDirectory(
            self,
            "Select Toolkit Data Folder",
            self.toolkit_input.text(),
            QFileDialog.Option.ShowDirsOnly,
        )
        if folder:
            self.toolkit_input.setText(folder)

    def validate_hytale_path(self):
        """Validate the Hytale installation path."""
        path = self.hytale_input.text().strip()

        if not path:
            self.hytale_status.setText("")
            self.checklist_card.hide()
            self.hytale_path = None
            self._update_button()
            return

        path_obj = Path(path)
        self.checklist_card.show()

        # Check each required item
        client_ok = (path_obj / "Client").exists()
        server_ok = (path_obj / "Server").exists()
        assets_ok = (path_obj / "Assets.zip").exists()

        check = "\u2714"  # ✔
        cross = "\u2718"  # ✘

        self.client_check.setText(f"{check if client_ok else cross}  Client/  -  {'Found' if client_ok else 'Missing'}")
        self.client_check.setStyleSheet(
            f"font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 12px; color: {'#22C55E' if client_ok else '#EF4444'};"
        )

        self.server_check.setText(f"{check if server_ok else cross}  Server/  -  {'Found' if server_ok else 'Missing'}")
        self.server_check.setStyleSheet(
            f"font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 12px; color: {'#22C55E' if server_ok else '#EF4444'};"
        )

        self.assets_check.setText(f"{check if assets_ok else cross}  Assets.zip  -  {'Found' if assets_ok else 'Missing'}")
        self.assets_check.setStyleSheet(
            f"font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 12px; color: {'#22C55E' if assets_ok else '#EF4444'};"
        )

        if client_ok and server_ok and assets_ok:
            self.hytale_status.setText("\u2714  Valid Hytale installation")
            self.hytale_status.setStyleSheet("font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 12px; color: #22C55E; font-weight: bold;")
            self.hytale_path = path
        elif not path_obj.exists():
            self.hytale_status.setText("\u2718  Folder does not exist")
            self.hytale_status.setStyleSheet("font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 12px; color: #EF4444;")
            self.hytale_path = None
        else:
            self.hytale_status.setText("\u2718  Missing required files")
            self.hytale_status.setStyleSheet("font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 12px; color: #EF4444;")
            self.hytale_path = None

        self._update_button()

    def validate_toolkit_path(self):
        """Validate the toolkit data path and check if toolkit is downloaded."""
        path = self.toolkit_input.text().strip()

        if not path:
            self.toolkit_status.setText("\u2718  Path cannot be empty")
            self.toolkit_status.setStyleSheet("font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 12px; color: #EF4444;")
            self.toolkit_path = None
            self._toolkit_downloaded = False
            self.download_status.hide()
            self._update_button()
            return

        path_obj = Path(path)

        # Check if path is valid (exists or can be created)
        if path_obj.exists():
            self.toolkit_status.setText("\u2714  Folder exists")
            self.toolkit_status.setStyleSheet("font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 12px; color: #22C55E;")
            self.toolkit_path = path

            # Check if toolkit is already downloaded
            self._toolkit_downloaded = self._check_toolkit_installed(path_obj)
            if self._toolkit_downloaded:
                self.download_status.setText("\u2714  Toolkit installed")
                self.download_status.setStyleSheet("font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 12px; color: #22C55E; font-weight: bold;")
                self.download_status.show()
                self._state = "completed"
            else:
                self.download_status.hide()
                self._state = "idle"
        else:
            # Check if parent exists (we can create the folder)
            if path_obj.parent.exists():
                self.toolkit_status.setText("\u2714  Folder will be created")
                self.toolkit_status.setStyleSheet("font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 12px; color: #22C55E;")
                self.toolkit_path = path
                self._toolkit_downloaded = False
                self.download_status.hide()
                self._state = "idle"
            else:
                self.toolkit_status.setText("\u2718  Parent folder does not exist")
                self.toolkit_status.setStyleSheet("font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 12px; color: #EF4444;")
                self.toolkit_path = None
                self._toolkit_downloaded = False
                self.download_status.hide()

        self._update_button()

    def _check_toolkit_installed(self, path: Path) -> bool:
        """Check if the toolkit is already installed at the given path."""
        # Look for key files that indicate the toolkit is installed
        key_files = [
            path / "init-mod.py",
            path / "hytale-rag" / "setup.py",
            path / "tools" / "vineflower.jar",
        ]
        # Need at least 2 of these files to consider it installed
        found = sum(1 for f in key_files if f.exists())
        return found >= 2

    def _update_button(self):
        """Trigger button state update."""
        if self._button_callback:
            self._button_callback()

    def can_proceed(self) -> bool:
        """Check if user can proceed to next page."""
        return self.hytale_path is not None and self.toolkit_path is not None

    def get_paths(self) -> dict:
        """Return the selected paths."""
        return {
            "hytale_path": self.hytale_path,
            "toolkit_path": self.toolkit_path,
        }

    def set_button_callback(self, callback):
        """Set callback for button state updates."""
        self._button_callback = callback

    def get_next_button_config(self) -> dict:
        """Get next button configuration based on state."""
        if not self.can_proceed():
            return {"text": "Next", "style": "disabled", "enabled": False}
        elif self._state == "downloading":
            return {"text": "Downloading...", "style": "disabled", "enabled": False}
        elif self._toolkit_downloaded or self._state == "completed":
            return {"text": "Next", "style": "primary", "enabled": True}
        else:
            # Toolkit not downloaded - show Install button
            return {"text": "Install", "style": "action", "enabled": True}

    def should_run_action(self) -> bool:
        """Check if clicking Next should trigger download instead of navigation."""
        return (
            self.can_proceed()
            and not self._toolkit_downloaded
            and self._state == "idle"
        )

    def start_download(self):
        """Start downloading the toolkit from GitHub."""
        if self._state == "downloading":
            return

        self._state = "downloading"
        self._last_error = ""
        self.state_changed.emit("downloading")

        # Show animated downloading status
        self.download_status.show()
        self._dot_count = 0
        self._animate_dots()

        # Start dot animation timer
        self._dot_timer = QTimer(self)
        self._dot_timer.timeout.connect(self._animate_dots)
        self._dot_timer.start(400)

        self._update_button()
        if self._back_button_callback:
            self._back_button_callback()

        toolkit_path = Path(self.toolkit_path)

        # Ensure parent directory exists
        toolkit_path.parent.mkdir(parents=True, exist_ok=True)

        # If folder exists but is empty, remove it (git clone needs to create it)
        if toolkit_path.exists():
            try:
                # Check if empty
                if not any(toolkit_path.iterdir()):
                    toolkit_path.rmdir()
            except (OSError, StopIteration):
                pass

        # Clone from GitHub
        self._process = QProcess(self)
        self._process.setWorkingDirectory(str(toolkit_path.parent))
        self._process.readyReadStandardError.connect(self._handle_git_stderr)
        self._process.finished.connect(self._handle_download_finished)

        # Use git clone
        repo_url = "https://github.com/logan-mcduffie/Hytale-Toolkit.git"
        folder_name = toolkit_path.name

        # On Windows, use cmd /c to properly find git in PATH
        if sys.platform == "win32":
            self._process.start("cmd", ["/c", "git", "clone", "--depth", "1", repo_url, folder_name])
        else:
            self._process.start("git", ["clone", "--depth", "1", repo_url, folder_name])

    def _handle_git_stderr(self):
        """Capture git stderr for error messages."""
        if self._process:
            data = self._process.readAllStandardError()
            text = bytes(data).decode("utf-8", errors="replace").strip()
            if text:
                self._last_error = text

    def _animate_dots(self):
        """Animate the downloading dots."""
        self._dot_count = (self._dot_count + 1) % 4
        dots = "." * self._dot_count + " " * (3 - self._dot_count)
        self.download_status.setText(f"Downloading files{dots}")
        self.download_status.setStyleSheet("font-size: 12px; color: #3498db;")

    def _handle_download_finished(self, exit_code: int, exit_status):
        """Handle download completion."""
        # Stop animation
        if self._dot_timer:
            self._dot_timer.stop()
            self._dot_timer = None

        self._process = None

        if exit_code == 0:
            self._state = "completed"
            self._toolkit_downloaded = True
            self.download_status.setText("\u2714  Toolkit downloaded!")
            self.download_status.setStyleSheet("font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 12px; color: #22C55E; font-weight: bold;")
        else:
            self._state = "idle"
            self._toolkit_downloaded = False
            # Show helpful error message
            error_msg = getattr(self, '_last_error', '')
            if "already exists" in error_msg:
                self.download_status.setText("\u2718  Folder not empty - clear it first")
                self.download_status.setStyleSheet("font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 12px; color: #EF4444;")
            elif "not found" in error_msg.lower() or exit_code == 1 and not error_msg:
                # Git not found - show link to download
                git_url = "https://git-scm.com/downloads"
                self.download_status.setText(
                    f'\u2718  Git required. <a href="{git_url}" style="color: #3498db;">Download Git</a> and try again.'
                )
                self.download_status.setStyleSheet("font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 12px; color: #EF4444;")
            else:
                self.download_status.setText(f"\u2718  Download failed (code {exit_code})")
                self.download_status.setStyleSheet("font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 12px; color: #EF4444;")

        self.state_changed.emit(self._state)
        self._update_button()
        if self._back_button_callback:
            self._back_button_callback()

    def get_state(self) -> str:
        """Get current page state."""
        return self._state

    def get_back_button_config(self) -> dict:
        """Get back button configuration based on state."""
        if self._state == "downloading":
            return {"text": "Cancel", "style": "danger", "enabled": True}
        else:
            return {"text": "Back", "style": "default", "enabled": True}

    def set_back_button_callback(self, callback):
        """Set callback for back button state updates."""
        self._back_button_callback = callback

    def cancel_download(self):
        """Cancel the download process."""
        if self._dot_timer:
            self._dot_timer.stop()
            self._dot_timer = None

        if self._process and self._process.state() == QProcess.ProcessState.Running:
            self._process.kill()
            self._process.waitForFinished(1000)
        self._process = None

        self._state = "idle"
        self.download_status.hide()
        self.state_changed.emit("idle")
        self._update_button()
        if hasattr(self, '_back_button_callback') and self._back_button_callback:
            self._back_button_callback()


class DecompilePage(QWidget):
    """Page for decompilation settings with terminal output."""

    # Signals for state changes
    state_changed = pyqtSignal(str)  # idle, running, completed, failed

    def __init__(self, parent=None):
        super().__init__(parent)
        self._button_callback = None
        self._back_button_callback = None
        self._state = "idle"  # idle, running, completed, failed, installing_java
        self._process = None
        self._hytale_path = None
        self._toolkit_path = None
        self._log_file_path = None
        self._class_count = 0
        self._has_existing = False
        self._use_existing = True  # Default to using existing if found
        self._java_installed = False
        self._java_version = ""
        self._java_major = 0
        self._jdk_download_path = None

        layout = QVBoxLayout(self)
        layout.setContentsMargins(40, 40, 40, 30)

        # Title
        self.title = QLabel("Decompile Hytale")
        self.title.setStyleSheet("font-size: 22px; font-weight: bold; color: white;")
        self.title.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(self.title)

        # Description (changes based on state)
        self.desc = QLabel(
            "Decompile the Hytale client and server JARs to browse\n"
            "and search the source code. This step is optional but recommended."
        )
        self.desc.setStyleSheet("color: #aaaaaa; font-size: 13px;")
        self.desc.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.desc.setWordWrap(True)
        layout.addWidget(self.desc)

        layout.addSpacing(15)

        # ===== Java Status Section =====
        self.java_section = QWidget()
        java_layout = QVBoxLayout(self.java_section)
        java_layout.setContentsMargins(0, 0, 0, 0)
        java_layout.setSpacing(12)

        # Java status banner (warning - shown when Java not found)
        self.java_banner = QWidget()
        java_banner_layout = QVBoxLayout(self.java_banner)
        java_banner_layout.setContentsMargins(0, 0, 0, 0)
        java_banner_layout.setSpacing(12)

        # Warning icon and text
        self.java_status_label = QLabel("⚠️ Java Development Kit (JDK) not found")
        self.java_status_label.setStyleSheet(
            "font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 14px; font-weight: bold; color: #e74c3c;"
        )
        self.java_status_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        java_banner_layout.addWidget(self.java_status_label)

        self.java_hint = QLabel("JDK is required for decompiling and generating Javadocs.")
        self.java_hint.setStyleSheet("font-size: 12px; color: #888888;")
        self.java_hint.setAlignment(Qt.AlignmentFlag.AlignCenter)
        java_banner_layout.addWidget(self.java_hint)

        # Install button
        self.install_java_btn = QPushButton("Install JDK 25")
        self.install_java_btn.setFixedHeight(40)
        self.install_java_btn.setFixedWidth(200)
        self.install_java_btn.setCursor(Qt.CursorShape.PointingHandCursor)
        self.install_java_btn.setStyleSheet("""
            QPushButton {
                background-color: #e74c3c;
                color: white;
                border: none;
                border-radius: 6px;
                font-size: 13px;
                font-weight: bold;
            }
            QPushButton:hover {
                background-color: #c0392b;
            }
            QPushButton:disabled {
                background-color: #555555;
                color: #888888;
            }
        """)
        self.install_java_btn.clicked.connect(self._start_java_install)

        # Center the button
        btn_container = QWidget()
        btn_layout = QHBoxLayout(btn_container)
        btn_layout.setContentsMargins(0, 5, 0, 5)
        btn_layout.addStretch()
        btn_layout.addWidget(self.install_java_btn)
        btn_layout.addStretch()
        java_banner_layout.addWidget(btn_container)

        # Progress bar for download (hidden initially)
        self.java_progress = QProgressBar()
        self.java_progress.setFixedHeight(6)
        self.java_progress.setRange(0, 100)
        self.java_progress.setTextVisible(False)
        self.java_progress.setStyleSheet("""
            QProgressBar {
                background-color: #2a2a2a;
                border: none;
                border-radius: 3px;
            }
            QProgressBar::chunk {
                background-color: #3498db;
                border-radius: 3px;
            }
        """)
        self.java_progress.hide()
        java_banner_layout.addWidget(self.java_progress)

        self.java_progress_label = QLabel("")
        self.java_progress_label.setStyleSheet("font-size: 11px; color: #888888;")
        self.java_progress_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.java_progress_label.hide()
        java_banner_layout.addWidget(self.java_progress_label)

        java_layout.addWidget(self.java_banner)

        # Success banner (hidden by default, shown when Java is found)
        self.java_success_banner = QLabel()
        self.java_success_banner.setStyleSheet("""
            font-family: 'Segoe UI Symbol', 'Segoe UI';
            font-size: 13px;
            color: #22C55E;
        """)
        self.java_success_banner.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.java_success_banner.hide()
        java_layout.addWidget(self.java_success_banner)

        layout.addWidget(self.java_section)
        self.java_section.hide()  # Hidden until we check Java status

        layout.addSpacing(5)

        # Stacked widget for settings/terminal views
        self.stack = QStackedWidget()
        layout.addWidget(self.stack, 1)  # Give it stretch

        # ===== Settings View (index 0) =====
        self.settings_view = QWidget()
        settings_layout = QVBoxLayout(self.settings_view)
        settings_layout.setContentsMargins(0, 15, 0, 0)
        settings_layout.setSpacing(25)

        # Main content container (centered)
        content_container = QWidget()
        content_layout = QVBoxLayout(content_container)
        content_layout.setContentsMargins(0, 0, 0, 0)
        content_layout.setSpacing(25)

        # ===== Existing Installation Banner (hidden by default) =====
        self.existing_banner = QWidget()
        self.existing_banner.hide()
        existing_layout = QVBoxLayout(self.existing_banner)
        existing_layout.setContentsMargins(0, 0, 0, 10)
        existing_layout.setSpacing(12)

        # Banner header with icon
        banner_header = QLabel("✓ Existing decompiled code found")
        banner_header.setStyleSheet("""
            font-family: 'Segoe UI Symbol', 'Segoe UI';
            font-size: 14px;
            font-weight: bold;
            color: #2ecc71;
            padding: 12px 16px;
            background-color: rgba(46, 204, 113, 0.15);
            border-radius: 8px;
        """)
        banner_header.setAlignment(Qt.AlignmentFlag.AlignCenter)
        existing_layout.addWidget(banner_header)

        # Option buttons container
        options_container = QWidget()
        options_layout = QHBoxLayout(options_container)
        options_layout.setContentsMargins(0, 0, 0, 0)
        options_layout.setSpacing(10)

        # Use Existing button
        self.use_existing_btn = QPushButton("Use Existing")
        self.use_existing_btn.setCheckable(True)
        self.use_existing_btn.setChecked(True)
        self.use_existing_btn.setFixedHeight(40)
        self.use_existing_btn.setStyleSheet("""
            QPushButton {
                background-color: #1f6aa5;
                color: white;
                border: 2px solid #1f6aa5;
                border-radius: 6px;
                padding: 0px 20px;
                font-size: 13px;
                font-weight: bold;
            }
            QPushButton:hover {
                background-color: #2980b9;
                border-color: #2980b9;
            }
            QPushButton:checked {
                background-color: #1f6aa5;
                border-color: #3498db;
            }
        """)
        self.use_existing_btn.clicked.connect(lambda: self._set_use_existing(True))
        options_layout.addWidget(self.use_existing_btn)

        # Reinstall button
        self.reinstall_btn = QPushButton("Reinstall")
        self.reinstall_btn.setCheckable(True)
        self.reinstall_btn.setFixedHeight(40)
        self.reinstall_btn.setStyleSheet("""
            QPushButton {
                background-color: transparent;
                color: #aaaaaa;
                border: 2px solid #555555;
                border-radius: 6px;
                padding: 0px 20px;
                font-size: 13px;
                font-weight: bold;
            }
            QPushButton:hover {
                background-color: #3a3a3a;
                color: white;
                border-color: #666666;
            }
            QPushButton:checked {
                background-color: #3a3a3a;
                color: white;
                border-color: #e74c3c;
            }
        """)
        self.reinstall_btn.clicked.connect(lambda: self._set_use_existing(False))
        options_layout.addWidget(self.reinstall_btn)

        existing_layout.addWidget(options_container)
        content_layout.addWidget(self.existing_banner)

        # ===== Decompile Options Container (hidden when using existing) =====
        self.decompile_options = QWidget()
        decompile_options_layout = QVBoxLayout(self.decompile_options)
        decompile_options_layout.setContentsMargins(0, 0, 0, 0)
        decompile_options_layout.setSpacing(25)

        # Enable decompilation toggle row (only shown for fresh installs, not reinstalls)
        self.decompile_toggle_row = QWidget()
        toggle_layout = QHBoxLayout(self.decompile_toggle_row)
        toggle_layout.setContentsMargins(0, 0, 0, 0)
        toggle_layout.setSpacing(12)

        self.decompile_toggle = ToggleSwitch(checked=True)
        self.decompile_toggle.connect_toggled(self.toggle_ram_slider)
        toggle_layout.addWidget(self.decompile_toggle)

        toggle_text = QWidget()
        toggle_text_layout = QVBoxLayout(toggle_text)
        toggle_text_layout.setContentsMargins(0, 0, 0, 0)
        toggle_text_layout.setSpacing(2)

        toggle_label = QLabel("Enable decompilation")
        toggle_label.setStyleSheet("font-size: 14px; font-weight: bold; color: white;")
        toggle_label.setMinimumHeight(20)
        toggle_text_layout.addWidget(toggle_label)

        toggle_hint = QLabel("Recommended for full code search capabilities")
        toggle_hint.setStyleSheet("font-size: 11px; color: #888888;")
        toggle_hint.setMinimumHeight(16)
        toggle_text_layout.addWidget(toggle_hint)

        toggle_layout.addWidget(toggle_text)
        toggle_layout.addStretch()
        decompile_options_layout.addWidget(self.decompile_toggle_row)

        # RAM allocation section
        self.ram_section = QWidget()
        ram_layout = QVBoxLayout(self.ram_section)
        ram_layout.setContentsMargins(0, 0, 0, 0)
        ram_layout.setSpacing(15)

        ram_header = QHBoxLayout()
        ram_label = QLabel("RAM Allocation")
        ram_label.setStyleSheet("font-size: 14px; font-weight: bold; color: white;")
        ram_label.setMinimumHeight(20)
        ram_header.addWidget(ram_label)

        self.ram_value_label = QLabel("8 GB")
        self.ram_value_label.setStyleSheet(
            "font-size: 14px; color: #3498db; font-weight: bold;"
        )
        self.ram_value_label.setMinimumHeight(20)
        ram_header.addStretch()
        ram_header.addWidget(self.ram_value_label)
        ram_layout.addLayout(ram_header)

        # RAM slider
        self.ram_slider = QSlider(Qt.Orientation.Horizontal)
        self.ram_slider.setMinimum(2)
        self.ram_slider.setMaximum(16)
        self.ram_slider.setValue(8)
        self.ram_slider.setStyleSheet("""
            QSlider::groove:horizontal {
                height: 8px;
                background: #3a3a3a;
                border-radius: 4px;
            }
            QSlider::handle:horizontal {
                background: #3498db;
                width: 20px;
                height: 20px;
                margin: -6px 0;
                border-radius: 10px;
            }
            QSlider::handle:horizontal:hover {
                background: #5dade2;
            }
            QSlider::sub-page:horizontal {
                background: #3498db;
                border-radius: 4px;
            }
        """)
        self.ram_slider.valueChanged.connect(self.update_ram_label)
        ram_layout.addWidget(self.ram_slider)

        # Min/Max labels
        minmax_layout = QHBoxLayout()
        min_label = QLabel("2 GB")
        min_label.setStyleSheet("font-size: 11px; color: #666666;")
        min_label.setMinimumHeight(16)
        minmax_layout.addWidget(min_label)
        minmax_layout.addStretch()
        max_label = QLabel("16 GB")
        max_label.setStyleSheet("font-size: 11px; color: #666666;")
        max_label.setMinimumHeight(16)
        minmax_layout.addWidget(max_label)
        ram_layout.addLayout(minmax_layout)

        # Hint
        hint = QLabel("More RAM speeds up decompilation but uses more memory")
        hint.setStyleSheet("font-size: 11px; color: #666666;")
        hint.setWordWrap(True)
        hint.setMinimumHeight(18)
        ram_layout.addWidget(hint)

        # Time estimate (right below hint)
        self.time_estimate = QLabel("Estimated time: 2-5 minutes")
        self.time_estimate.setStyleSheet("font-size: 11px; color: #888888; margin-top: 8px;")
        ram_layout.addWidget(self.time_estimate)

        # Add opacity effect to RAM section for smooth toggle
        self.ram_opacity = QGraphicsOpacityEffect(self.ram_section)
        self.ram_opacity.setOpacity(1.0)
        self.ram_section.setGraphicsEffect(self.ram_opacity)

        decompile_options_layout.addWidget(self.ram_section)
        content_layout.addWidget(self.decompile_options)

        # Center the content
        center_container = QWidget()
        center_layout = QHBoxLayout(center_container)
        center_layout.setContentsMargins(0, 0, 0, 0)
        center_layout.addStretch()
        content_container.setFixedWidth(340)
        center_layout.addWidget(content_container)
        center_layout.addStretch()
        settings_layout.addWidget(center_container)

        settings_layout.addStretch()

        self.stack.addWidget(self.settings_view)

        # ===== Terminal View (index 1) =====
        self.terminal_view = QWidget()
        terminal_layout = QVBoxLayout(self.terminal_view)
        terminal_layout.setContentsMargins(0, 0, 0, 0)
        terminal_layout.setSpacing(10)

        # Terminal widget
        self.terminal = TerminalWidget()
        terminal_layout.addWidget(self.terminal, 1)

        # Progress info row
        self.progress_row = QWidget()
        progress_layout = QHBoxLayout(self.progress_row)
        progress_layout.setContentsMargins(0, 0, 0, 0)

        self.progress_label = QLabel("Initializing...")
        self.progress_label.setStyleSheet("font-size: 12px; color: #888888;")
        progress_layout.addWidget(self.progress_label)
        progress_layout.addStretch()

        self.class_count_label = QLabel("")
        self.class_count_label.setStyleSheet("font-size: 12px; color: #3498db; font-weight: bold;")
        progress_layout.addWidget(self.class_count_label)

        terminal_layout.addWidget(self.progress_row)

        # Error action row (hidden by default)
        self.error_actions = QWidget()
        self.error_actions.hide()
        error_layout = QHBoxLayout(self.error_actions)
        error_layout.setContentsMargins(0, 10, 0, 0)
        error_layout.setSpacing(10)

        self.open_log_btn = QPushButton("Open Log File")
        self.open_log_btn.setStyleSheet("""
            QPushButton {
                background-color: transparent;
                color: #aaaaaa;
                border: 1px solid #555555;
                border-radius: 6px;
                padding: 8px 16px;
                font-size: 12px;
            }
            QPushButton:hover {
                background-color: #3a3a3a;
                color: white;
            }
        """)
        self.open_log_btn.clicked.connect(self.open_log_file)
        error_layout.addWidget(self.open_log_btn)

        self.retry_btn = QPushButton("Retry")
        self.retry_btn.setStyleSheet("""
            QPushButton {
                background-color: #1f6aa5;
                color: white;
                border: none;
                border-radius: 6px;
                padding: 8px 16px;
                font-size: 12px;
            }
            QPushButton:hover {
                background-color: #2980b9;
            }
        """)
        self.retry_btn.clicked.connect(self.retry_decompile)
        error_layout.addWidget(self.retry_btn)

        error_layout.addStretch()
        terminal_layout.addWidget(self.error_actions)

        self.stack.addWidget(self.terminal_view)

    def set_paths(self, hytale_path: str, toolkit_path: str):
        """Set the paths needed for decompilation."""
        self._hytale_path = hytale_path
        self._toolkit_path = toolkit_path

        # Check for Java installation
        self._check_java()

        # Check for existing decompiled code
        if toolkit_path:
            decompiled_dir = Path(toolkit_path) / "decompiled"
            if decompiled_dir.exists() and any(decompiled_dir.iterdir()):
                self._has_existing = True
                self._use_existing = True
                self.existing_banner.show()
                self.decompile_options.hide()
                self.time_estimate.hide()
            else:
                # Fresh install - show toggle so user can choose to skip
                self._has_existing = False
                self.existing_banner.hide()
                self.decompile_options.show()
                self.decompile_toggle_row.show()
                self.time_estimate.show()
                self.decompile_toggle.setChecked(True)
                self.toggle_ram_slider(True)

        # Update button state
        if self._button_callback:
            self._button_callback()

    def _set_use_existing(self, use_existing: bool):
        """Toggle between using existing installation or reinstalling."""
        self._use_existing = use_existing

        # Update button visual states
        self.use_existing_btn.setChecked(use_existing)
        self.reinstall_btn.setChecked(not use_existing)

        # Update button styles
        if use_existing:
            self.use_existing_btn.setStyleSheet("""
                QPushButton {
                    background-color: #1f6aa5;
                    color: white;
                    border: 2px solid #3498db;
                    border-radius: 6px;
                    padding: 0px 20px;
                    font-size: 13px;
                    font-weight: bold;
                }
                QPushButton:hover {
                    background-color: #2980b9;
                    border-color: #5dade2;
                }
            """)
            self.reinstall_btn.setStyleSheet("""
                QPushButton {
                    background-color: transparent;
                    color: #aaaaaa;
                    border: 2px solid #555555;
                    border-radius: 6px;
                    padding: 0px 20px;
                    font-size: 13px;
                    font-weight: bold;
                }
                QPushButton:hover {
                    background-color: #3a3a3a;
                    color: white;
                    border-color: #666666;
                }
            """)
            # Hide decompile options when using existing
            self.decompile_options.hide()
            self.time_estimate.hide()
        else:
            self.use_existing_btn.setStyleSheet("""
                QPushButton {
                    background-color: transparent;
                    color: #aaaaaa;
                    border: 2px solid #555555;
                    border-radius: 6px;
                    padding: 0px 20px;
                    font-size: 13px;
                    font-weight: bold;
                }
                QPushButton:hover {
                    background-color: #3a3a3a;
                    color: white;
                    border-color: #666666;
                }
            """)
            self.reinstall_btn.setStyleSheet("""
                QPushButton {
                    background-color: #c0392b;
                    color: white;
                    border: 2px solid #e74c3c;
                    border-radius: 6px;
                    padding: 0px 20px;
                    font-size: 13px;
                    font-weight: bold;
                }
                QPushButton:hover {
                    background-color: #e74c3c;
                    border-color: #ec7063;
                }
            """)
            # Show RAM options when reinstalling (no toggle needed - they chose to reinstall)
            self.decompile_options.show()
            self.decompile_toggle_row.hide()  # Hide toggle - reinstall implies they want to decompile
            self.time_estimate.show()
            self.decompile_toggle.setChecked(True)
            self.toggle_ram_slider(True)

        # Notify wizard to update button text
        if self._button_callback:
            self._button_callback()

    def toggle_ram_slider(self, enabled: bool):
        """Enable/disable RAM slider based on toggle."""
        self.ram_section.setEnabled(enabled)
        self.ram_opacity.setOpacity(1.0 if enabled else 0.4)
        self.time_estimate.setEnabled(enabled)
        self.time_estimate.setStyleSheet(
            f"font-size: 12px; color: {'#666666' if enabled else '#444444'};"
        )
        # Notify wizard to update button text
        if self._button_callback:
            self._button_callback()

    def _check_java(self):
        """Check if Java is installed and update UI accordingly."""
        self._local_java_path = None

        # First check for local JDK in toolkit directory
        if self._toolkit_path:
            jdk_dir = Path(self._toolkit_path) / "jdk"
            if jdk_dir.exists():
                # Find the nested JDK folder
                subdirs = [d for d in jdk_dir.iterdir() if d.is_dir()]
                if subdirs:
                    if sys.platform == "win32":
                        java_exe = subdirs[0] / "bin" / "java.exe"
                    else:
                        java_exe = subdirs[0] / "bin" / "java"

                    if java_exe.exists():
                        self._local_java_path = str(java_exe)
                        self._java_installed = True
                        self._java_version = f"JDK 25 (toolkit/jdk)"
                        self._java_major = 25

                        self.java_section.show()
                        self.java_banner.hide()
                        self.java_success_banner.setText(f"✓ {self._java_version}")
                        self.java_success_banner.show()
                        return

        # Fall back to system Java
        self._java_installed, self._java_version, self._java_major = check_java_installed()

        self.java_section.show()

        if self._java_installed and self._java_major >= 21:
            # Java is good - show success banner
            self.java_banner.hide()
            self.java_success_banner.setText(f"✓ {self._java_version}")
            self.java_success_banner.show()
        elif self._java_installed:
            # Java installed but version too old
            self.java_success_banner.hide()
            self.java_banner.show()
            self.java_status_label.setText(f"⚠️ Java {self._java_major} found - JDK 21+ required")
            self.java_hint.setText("Please install JDK 21 or newer for decompilation.")
            self.install_java_btn.show()
        else:
            # Java not found
            self.java_success_banner.hide()
            self.java_banner.show()
            self.java_status_label.setText("⚠️ Java Development Kit (JDK) not found")
            self.java_hint.setText("JDK is required for decompiling and generating Javadocs.")
            self.install_java_btn.show()

    def _start_java_install(self):
        """Start downloading and installing JDK."""
        self._state = "installing_java"
        self.install_java_btn.setEnabled(False)
        self.install_java_btn.setText("Downloading...")
        self.java_progress.setValue(0)
        self.java_progress.show()
        self.java_progress_label.setText("Connecting to Adoptium...")
        self.java_progress_label.show()

        # Get download info
        info = get_adoptium_download_info()

        # Download directory
        downloads_dir = Path(self._toolkit_path or ".") / "downloads"
        downloads_dir.mkdir(parents=True, exist_ok=True)
        self._jdk_download_path = downloads_dir / info["filename"]

        # Start download in a thread
        import threading
        thread = threading.Thread(
            target=self._download_jdk,
            args=(info["url"], self._jdk_download_path),
            daemon=True
        )
        thread.start()

    def _download_jdk(self, url: str, dest: Path):
        """Download JDK in background thread."""
        try:
            req = urllib.request.Request(
                url,
                headers={"User-Agent": "Hytale-Toolkit"}
            )

            with urllib.request.urlopen(req, timeout=30) as response:
                total_size = int(response.headers.get('Content-Length', 0))
                downloaded = 0
                chunk_size = 1024 * 1024  # 1MB chunks

                with open(dest, 'wb') as f:
                    while True:
                        chunk = response.read(chunk_size)
                        if not chunk:
                            break
                        f.write(chunk)
                        downloaded += len(chunk)

                        if total_size > 0:
                            percent = int(downloaded * 100 / total_size)
                            mb_downloaded = downloaded / (1024 * 1024)
                            mb_total = total_size / (1024 * 1024)
                            # Use QTimer to update UI from main thread
                            QTimer.singleShot(0, lambda p=percent, d=mb_downloaded, t=mb_total:
                                self._update_download_progress(p, d, t))

            # Download complete - trigger install
            QTimer.singleShot(0, self._run_jdk_installer)

        except Exception as e:
            QTimer.singleShot(0, lambda: self._handle_java_install_error(str(e)))

    def _update_download_progress(self, percent: int, downloaded_mb: float, total_mb: float):
        """Update download progress UI."""
        self.java_progress.setValue(percent)
        self.java_progress_label.setText(f"Downloading: {downloaded_mb:.1f} MB / {total_mb:.1f} MB")

    def _run_jdk_installer(self):
        """Extract the JDK archive after download."""
        self.java_progress_label.setText("Extracting JDK...")
        self.java_progress.setRange(0, 0)  # Indeterminate

        # Run extraction in background thread
        import threading
        thread = threading.Thread(target=self._extract_jdk, daemon=True)
        thread.start()

    def _extract_jdk(self):
        """Extract JDK archive to toolkit directory."""
        try:
            # Extract to toolkit/jdk directory
            jdk_dir = Path(self._toolkit_path) / "jdk"

            # Remove existing if present
            if jdk_dir.exists():
                shutil.rmtree(jdk_dir)
            jdk_dir.mkdir(parents=True, exist_ok=True)

            if sys.platform == "win32":
                # Extract ZIP
                import zipfile
                with zipfile.ZipFile(self._jdk_download_path, 'r') as zip_ref:
                    zip_ref.extractall(jdk_dir)
            else:
                # Extract tar.gz
                import tarfile
                with tarfile.open(self._jdk_download_path, "r:gz") as tar:
                    tar.extractall(jdk_dir)

            # Find the actual JDK folder (it's nested, e.g., jdk/jdk-25.0.1+8/)
            subdirs = [d for d in jdk_dir.iterdir() if d.is_dir()]
            if subdirs:
                # Store the path to the actual JDK
                self._extracted_jdk_path = subdirs[0]
            else:
                self._extracted_jdk_path = jdk_dir

            # Clean up download file
            if self._jdk_download_path.exists():
                self._jdk_download_path.unlink()

            QTimer.singleShot(0, self._handle_extract_success)

        except Exception as e:
            QTimer.singleShot(0, lambda: self._handle_java_install_error(str(e)))

    def _handle_extract_success(self):
        """Handle successful JDK extraction."""
        self._state = "idle"

        # Find java executable
        if sys.platform == "win32":
            java_exe = self._extracted_jdk_path / "bin" / "java.exe"
        else:
            java_exe = self._extracted_jdk_path / "bin" / "java"

        if java_exe.exists():
            # Success! Update UI
            self.java_progress.setRange(0, 100)
            self.java_progress.setValue(100)
            self.java_progress_label.setText("JDK installed successfully!")
            self.java_progress_label.setStyleSheet("font-size: 12px; color: #22C55E;")

            # Store the java path for use by decompiler
            self._local_java_path = str(java_exe)

            # Update the success banner
            self.java_banner.hide()
            self.java_success_banner.setText(f"✓ JDK 25 installed to toolkit/jdk")
            self.java_success_banner.show()
            self.java_progress.hide()
            self.java_progress_label.hide()

            self._java_installed = True
            self._java_major = 25
        else:
            self._handle_java_install_error("Could not find java executable after extraction")

    def _verify_java_after_install(self):
        """Verify Java installation and update UI."""
        self._check_java()

        # Clean up downloaded installer
        if self._jdk_download_path and self._jdk_download_path.exists():
            try:
                self._jdk_download_path.unlink()
            except Exception:
                pass

        if self._java_installed and self._java_major >= 21:
            # Success!
            self.java_progress.hide()
            self.java_progress_label.hide()
            self.install_java_btn.hide()
        else:
            # Java still not found - may need restart or install didn't complete
            self.java_progress_label.setText(
                "Java not detected yet. Complete the installer or restart your computer."
            )
            self.java_progress_label.setStyleSheet("font-size: 12px; color: #F59E0B;")
            self.install_java_btn.setText("Check Again")
            self.install_java_btn.setEnabled(True)
            try:
                self.install_java_btn.clicked.disconnect()
            except Exception:
                pass
            self.install_java_btn.clicked.connect(self._verify_java_after_install)

        if self._button_callback:
            self._button_callback()

    def _handle_java_install_error(self, error: str):
        """Handle JDK installation error."""
        self._state = "idle"
        self.java_progress.setRange(0, 100)
        self.java_progress.setValue(0)
        self.java_progress_label.setText(f"Error: {error}")
        self.java_progress_label.setStyleSheet("font-size: 11px; color: #e74c3c; background: transparent;")
        self.install_java_btn.setText("Retry Download")
        self.install_java_btn.setEnabled(True)

    def update_ram_label(self, value):
        """Update the RAM value display."""
        self.ram_value_label.setText(f"{value} GB")

    def set_button_callback(self, callback):
        """Set callback to notify wizard of button changes."""
        self._button_callback = callback

    def set_back_button_callback(self, callback):
        """Set callback for back button changes."""
        self._back_button_callback = callback

    def get_state(self) -> str:
        """Get current state."""
        return self._state

    def get_next_button_config(self) -> dict:
        """Return config for the next button based on current state."""
        if self._state == "running" or self._state == "installing_java":
            return {
                "text": "Running...",
                "style": "disabled",
                "enabled": False,
            }
        elif self._state == "completed":
            return {
                "text": "Next",
                "style": "primary",
                "enabled": True,
            }
        elif self._state == "failed":
            return {
                "text": "Next",
                "style": "primary",
                "enabled": True,  # Allow proceeding even on failure
            }
        else:  # idle
            # If using existing installation, just navigate
            if self._has_existing and self._use_existing:
                return {
                    "text": "Next",
                    "style": "primary",
                    "enabled": True,
                }
            elif self.decompile_toggle.isChecked():
                return {
                    "text": "Decompile",
                    "style": "action",
                    "enabled": True,
                }
            else:
                return {
                    "text": "Skip",
                    "style": "secondary",
                    "enabled": True,
                }

    def get_back_button_config(self) -> dict:
        """Return config for the back button based on current state."""
        if self._state == "running":
            return {
                "text": "Cancel",
                "style": "danger",
                "enabled": True,
            }
        else:
            return {
                "text": "Back",
                "style": "default",
                "enabled": True,
            }

    def should_run_action(self) -> bool:
        """Check if clicking Next should run an action instead of navigating."""
        # Don't run action if using existing installation
        if self._has_existing and self._use_existing:
            return False
        return self._state == "idle" and self.decompile_toggle.isChecked()

    def start_decompile(self):
        """Start the decompilation process."""
        if self._state == "running":
            return

        self._state = "running"
        self._class_count = 0

        # Switch to terminal view
        self.stack.setCurrentIndex(1)
        self.desc.setText("Decompiling Hytale server JAR...")
        self.terminal.clear_terminal()
        self.error_actions.hide()
        self.progress_label.setText("Starting...")
        self.class_count_label.setText("")

        # Notify wizard
        if self._button_callback:
            self._button_callback()
        if self._back_button_callback:
            self._back_button_callback()

        # Setup log file
        toolkit_path = Path(self._toolkit_path)
        logs_dir = toolkit_path / "logs"
        logs_dir.mkdir(parents=True, exist_ok=True)
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        self._log_file_path = logs_dir / f"decompile_{timestamp}.log"

        # Log header
        self.terminal.append_info("=" * 60)
        self.terminal.append_info("Hytale Toolkit - Decompilation Log")
        self.terminal.append_info(f"Started: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        self.terminal.append_info("=" * 60)
        self.terminal.append_line("")

        # Log settings
        ram_gb = self.ram_slider.value()
        self.terminal.append_line(f"Hytale Path: {self._hytale_path}")
        self.terminal.append_line(f"Toolkit Path: {self._toolkit_path}")
        self.terminal.append_line(f"RAM Allocation: {ram_gb} GB")
        self.terminal.append_line("")

        # Verify paths
        server_jar = Path(self._hytale_path) / "Server" / "HytaleServer.jar"
        vineflower_jar = toolkit_path / "tools" / "vineflower.jar"
        decompiled_dir = toolkit_path / "decompiled"

        if not server_jar.exists():
            self.terminal.append_error(f"ERROR: HytaleServer.jar not found at {server_jar}")
            self._finish_with_error("HytaleServer.jar not found")
            return

        if not vineflower_jar.exists():
            self.terminal.append_error(f"ERROR: Vineflower not found at {vineflower_jar}")
            self._finish_with_error("Vineflower decompiler not found")
            return

        # Clear existing decompiled directory if exists
        if decompiled_dir.exists():
            self.terminal.append_warning("Removing existing decompiled files...")
            shutil.rmtree(decompiled_dir)

        decompiled_dir.mkdir(parents=True, exist_ok=True)

        self.terminal.append_line(f"Source:  {server_jar}")
        self.terminal.append_line(f"Output:  {decompiled_dir}")
        self.terminal.append_line("")
        self.terminal.append_info("Starting Vineflower decompiler...")
        self.terminal.append_line("")

        # Start the Java process
        self._process = QProcess(self)
        self._process.setProcessChannelMode(QProcess.ProcessChannelMode.MergedChannels)
        self._process.readyReadStandardOutput.connect(self._handle_output)
        self._process.finished.connect(self._handle_finished)
        self._process.errorOccurred.connect(self._handle_error)

        # Build command
        args = [
            "-Xms2G",
            f"-Xmx{ram_gb}G",
            "-jar", str(vineflower_jar),
            "-dgs=1",  # Decompile generic signatures
            "-asc=1",  # ASCII string characters
            "-rsy=1",  # Remove synthetic class members
            str(server_jar),
            str(decompiled_dir)
        ]

        # Use local JDK if available, otherwise system Java
        java_cmd = self._local_java_path if self._local_java_path else "java"
        self._process.start(java_cmd, args)

    def cancel_decompile(self):
        """Cancel the running decompilation."""
        if self._process and self._state == "running":
            self.terminal.append_warning("\nCancelling decompilation...")
            self._process.kill()
            self._state = "idle"
            self.stack.setCurrentIndex(0)
            self.desc.setText(
                "Decompile the Hytale client and server JARs to browse\n"
                "and search the source code. This step is optional but recommended."
            )
            if self._button_callback:
                self._button_callback()
            if self._back_button_callback:
                self._back_button_callback()

    def _handle_output(self):
        """Handle process output."""
        if not self._process:
            return

        data = self._process.readAllStandardOutput().data().decode('utf-8', errors='replace')
        for line in data.splitlines():
            line = line.strip()
            if not line:
                continue

            # Parse progress
            if "Loading Class:" in line or "Decompiling class" in line:
                self._class_count += 1
                # Extract class name
                if "Loading Class:" in line:
                    class_name = line.split("Loading Class:")[-1].split()[0] if "Loading Class:" in line else ""
                else:
                    class_name = line.split()[-1] if line.split() else ""
                short_name = class_name.split("/")[-1] if "/" in class_name else class_name

                self.progress_label.setText(f"Processing: {short_name[:40]}")
                self.class_count_label.setText(f"{self._class_count:,} classes")

                # Only log every 100th class to avoid flooding
                if self._class_count % 100 == 0:
                    self.terminal.append_line(f"[{self._class_count:,}] {short_name}")
            elif "error" in line.lower() and "info:" not in line.lower():
                # Skip benign warnings
                if any(skip in line for skip in [
                    "Cannot copy entry META-INF",
                    "cannot be decomposed",
                    "Unable to simplify switch",
                ]):
                    continue
                self.terminal.append_error(line)
            elif "warn" in line.lower():
                self.terminal.append_warning(line)
            else:
                self.terminal.append_line(line)

    def _handle_finished(self, exit_code, exit_status):
        """Handle process completion."""
        self.terminal.append_line("")

        if exit_code == 0:
            self._state = "completed"
            self.terminal.append_success("=" * 60)
            self.terminal.append_success(f"Decompilation completed successfully!")
            self.terminal.append_success(f"Total classes processed: {self._class_count:,}")
            self.terminal.append_success("=" * 60)

            self.desc.setText("Decompilation completed successfully!")
            self.progress_label.setText("Complete!")
            self.progress_label.setStyleSheet("font-size: 12px; color: #22C55E; font-weight: bold;")
        else:
            self._finish_with_error(f"Process exited with code {exit_code}")

        # Save log file
        self._save_log()

        # Notify wizard
        if self._button_callback:
            self._button_callback()
        if self._back_button_callback:
            self._back_button_callback()

    def _handle_error(self, error):
        """Handle process error."""
        error_messages = {
            QProcess.ProcessError.FailedToStart: "Failed to start Java. Is Java installed?",
            QProcess.ProcessError.Crashed: "Process crashed unexpectedly",
            QProcess.ProcessError.Timedout: "Process timed out",
            QProcess.ProcessError.WriteError: "Write error",
            QProcess.ProcessError.ReadError: "Read error",
            QProcess.ProcessError.UnknownError: "Unknown error",
        }
        msg = error_messages.get(error, f"Error: {error}")
        self.terminal.append_error(f"\nERROR: {msg}")
        self._finish_with_error(msg)

    def _finish_with_error(self, error_msg: str):
        """Handle decompilation failure."""
        self._state = "failed"
        self.terminal.append_line("")
        self.terminal.append_error("=" * 60)
        self.terminal.append_error(f"Decompilation failed: {error_msg}")
        self.terminal.append_error("=" * 60)

        self.desc.setText("Decompilation failed. You can retry or continue without it.")
        self.progress_label.setText("Failed")
        self.progress_label.setStyleSheet("font-size: 12px; color: #EF4444; font-weight: bold;")
        self.error_actions.show()

        self._save_log()

        if self._button_callback:
            self._button_callback()
        if self._back_button_callback:
            self._back_button_callback()

    def _save_log(self):
        """Save the log to file."""
        if self._log_file_path:
            try:
                with open(self._log_file_path, 'w', encoding='utf-8') as f:
                    f.write(self.terminal.get_full_log())
            except Exception as e:
                self.terminal.append_warning(f"Failed to save log: {e}")

    def open_log_file(self):
        """Open the log file in the default text editor."""
        if self._log_file_path and self._log_file_path.exists():
            import subprocess
            if sys.platform == "win32":
                os.startfile(str(self._log_file_path))
            elif sys.platform == "darwin":
                subprocess.run(["open", str(self._log_file_path)])
            else:
                subprocess.run(["xdg-open", str(self._log_file_path)])

    def retry_decompile(self):
        """Retry the decompilation."""
        self._state = "idle"
        self.progress_label.setStyleSheet("font-size: 12px; color: #888888;")
        self.start_decompile()

    def get_settings(self) -> dict:
        """Return the decompilation settings."""
        return {
            "enabled": self.decompile_toggle.isChecked(),
            "ram_gb": self.ram_slider.value() if self.decompile_toggle.isChecked() else 0,
            "completed": self._state == "completed",
        }


class JavadocsPage(QWidget):
    """Page for Javadocs generation with terminal output."""

    # Signals for state changes
    state_changed = pyqtSignal(str)  # idle, running, completed, failed

    def __init__(self, parent=None):
        super().__init__(parent)
        self._button_callback = None
        self._back_button_callback = None
        self._state = "idle"  # idle, running, completed, failed
        self._process = None
        self._toolkit_path = None
        self._ram_gb = 8  # Will be set from DecompilePage settings
        self._local_java_path = None  # Path to local JDK if installed
        self._log_file_path = None
        self._file_count = 0
        self._total_files = 0
        self._generated_count = 0
        self._in_generating_phase = False
        self._has_existing = False
        self._use_existing = True  # Default to using existing if found

        layout = QVBoxLayout(self)
        layout.setContentsMargins(40, 40, 40, 30)

        # Title
        self.title = QLabel("Generate Javadocs")
        self.title.setStyleSheet("font-size: 22px; font-weight: bold; color: white;")
        self.title.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(self.title)

        # Description (changes based on state)
        self.desc = QLabel(
            "Generate browsable HTML documentation of all classes and methods.\n"
            "Useful for exploring the Hytale codebase in a browser."
        )
        self.desc.setStyleSheet("color: #aaaaaa; font-size: 13px;")
        self.desc.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.desc.setWordWrap(True)
        layout.addWidget(self.desc)

        layout.addSpacing(20)

        # Stacked widget for settings/terminal views
        self.stack = QStackedWidget()
        layout.addWidget(self.stack, 1)  # Give it stretch

        # ===== Settings View (index 0) =====
        self.settings_view = QWidget()
        settings_layout = QVBoxLayout(self.settings_view)
        settings_layout.setContentsMargins(0, 15, 0, 0)
        settings_layout.setSpacing(20)

        # Main content container (centered)
        content_container = QWidget()
        content_layout = QVBoxLayout(content_container)
        content_layout.setContentsMargins(0, 0, 0, 0)
        content_layout.setSpacing(20)

        # ===== Existing Installation Banner (hidden by default) =====
        self.existing_banner = QWidget()
        self.existing_banner.hide()
        existing_layout = QVBoxLayout(self.existing_banner)
        existing_layout.setContentsMargins(0, 0, 0, 10)
        existing_layout.setSpacing(12)

        # Banner header with icon
        banner_header = QLabel("✓ Existing Javadocs found")
        banner_header.setStyleSheet("""
            font-family: 'Segoe UI Symbol', 'Segoe UI';
            font-size: 14px;
            font-weight: bold;
            color: #2ecc71;
            padding: 12px 16px;
            background-color: rgba(46, 204, 113, 0.15);
            border-radius: 8px;
        """)
        banner_header.setAlignment(Qt.AlignmentFlag.AlignCenter)
        existing_layout.addWidget(banner_header)

        # Option buttons container
        options_container = QWidget()
        options_btn_layout = QHBoxLayout(options_container)
        options_btn_layout.setContentsMargins(0, 0, 0, 0)
        options_btn_layout.setSpacing(10)

        # Use Existing button
        self.use_existing_btn = QPushButton("Use Existing")
        self.use_existing_btn.setCheckable(True)
        self.use_existing_btn.setChecked(True)
        self.use_existing_btn.setFixedHeight(40)
        self.use_existing_btn.setStyleSheet("""
            QPushButton {
                background-color: #1f6aa5;
                color: white;
                border: 2px solid #1f6aa5;
                border-radius: 6px;
                padding: 0px 20px;
                font-size: 13px;
                font-weight: bold;
            }
            QPushButton:hover {
                background-color: #2980b9;
                border-color: #2980b9;
            }
            QPushButton:checked {
                background-color: #1f6aa5;
                border-color: #3498db;
            }
        """)
        self.use_existing_btn.clicked.connect(lambda: self._set_use_existing(True))
        options_btn_layout.addWidget(self.use_existing_btn)

        # Regenerate button
        self.regenerate_btn = QPushButton("Regenerate")
        self.regenerate_btn.setCheckable(True)
        self.regenerate_btn.setFixedHeight(40)
        self.regenerate_btn.setStyleSheet("""
            QPushButton {
                background-color: transparent;
                color: #aaaaaa;
                border: 2px solid #555555;
                border-radius: 6px;
                padding: 0px 20px;
                font-size: 13px;
                font-weight: bold;
            }
            QPushButton:hover {
                background-color: #3a3a3a;
                color: white;
                border-color: #666666;
            }
            QPushButton:checked {
                background-color: #3a3a3a;
                color: white;
                border-color: #e74c3c;
            }
        """)
        self.regenerate_btn.clicked.connect(lambda: self._set_use_existing(False))
        options_btn_layout.addWidget(self.regenerate_btn)

        existing_layout.addWidget(options_container)
        content_layout.addWidget(self.existing_banner)

        # ===== Javadocs Options Container (hidden when using existing) =====
        self.javadocs_options = QWidget()
        javadocs_options_layout = QVBoxLayout(self.javadocs_options)
        javadocs_options_layout.setContentsMargins(0, 0, 0, 0)
        javadocs_options_layout.setSpacing(20)

        # Enable Javadocs toggle row (only shown for fresh installs, not regenerate)
        self.javadocs_toggle_row = QWidget()
        toggle_layout = QHBoxLayout(self.javadocs_toggle_row)
        toggle_layout.setContentsMargins(0, 0, 0, 0)
        toggle_layout.setSpacing(12)

        self.javadocs_toggle = ToggleSwitch(checked=False)  # Off by default (optional)
        self.javadocs_toggle.connect_toggled(self.toggle_options)
        toggle_layout.addWidget(self.javadocs_toggle)

        toggle_text = QWidget()
        toggle_text_layout = QVBoxLayout(toggle_text)
        toggle_text_layout.setContentsMargins(0, 0, 0, 0)
        toggle_text_layout.setSpacing(2)

        toggle_label = QLabel("Generate Javadocs")
        toggle_label.setStyleSheet("font-size: 14px; font-weight: bold; color: white;")
        toggle_text_layout.addWidget(toggle_label)

        toggle_hint = QLabel("Optional - requires JDK (will download if needed)")
        toggle_hint.setStyleSheet("font-size: 11px; color: #888888;")
        toggle_text_layout.addWidget(toggle_hint)

        toggle_layout.addWidget(toggle_text)
        toggle_layout.addStretch()
        javadocs_options_layout.addWidget(self.javadocs_toggle_row)

        # Options section (shown when enabled)
        self.options_section = QWidget()
        options_layout = QVBoxLayout(self.options_section)
        options_layout.setContentsMargins(0, 0, 0, 0)
        options_layout.setSpacing(15)

        # Include private members option
        private_row = QWidget()
        private_layout = QHBoxLayout(private_row)
        private_layout.setContentsMargins(0, 0, 0, 0)
        private_layout.setSpacing(12)

        self.private_toggle = ToggleSwitch(checked=False)
        private_layout.addWidget(self.private_toggle)

        private_text = QWidget()
        private_text_layout = QVBoxLayout(private_text)
        private_text_layout.setContentsMargins(0, 0, 0, 0)
        private_text_layout.setSpacing(2)

        private_label = QLabel("Include private members")
        private_label.setStyleSheet("font-size: 14px; font-weight: bold; color: white;")
        private_text_layout.addWidget(private_label)

        private_hint = QLabel("Show private fields and methods in documentation")
        private_hint.setStyleSheet("font-size: 11px; color: #888888;")
        private_text_layout.addWidget(private_hint)

        private_layout.addWidget(private_text)
        private_layout.addStretch()
        options_layout.addWidget(private_row)

        # Add opacity effect to options section
        self.options_opacity = QGraphicsOpacityEffect(self.options_section)
        self.options_opacity.setOpacity(0.4)  # Start faded since toggle is off
        self.options_section.setGraphicsEffect(self.options_opacity)
        self.options_section.setEnabled(False)

        javadocs_options_layout.addWidget(self.options_section)
        content_layout.addWidget(self.javadocs_options)

        # Center the content
        center_container = QWidget()
        center_layout = QHBoxLayout(center_container)
        center_layout.setContentsMargins(0, 0, 0, 0)
        center_layout.addStretch()
        content_container.setFixedWidth(340)
        center_layout.addWidget(content_container)
        center_layout.addStretch()
        settings_layout.addWidget(center_container)

        settings_layout.addStretch()

        # Note at bottom
        self.note = QLabel("Note: Requires decompiled code from previous step")
        self.note.setStyleSheet("font-size: 12px; color: #666666;")
        self.note.setAlignment(Qt.AlignmentFlag.AlignCenter)
        settings_layout.addWidget(self.note)

        self.stack.addWidget(self.settings_view)

        # ===== Terminal View (index 1) =====
        self.terminal_view = QWidget()
        terminal_layout = QVBoxLayout(self.terminal_view)
        terminal_layout.setContentsMargins(0, 0, 0, 0)
        terminal_layout.setSpacing(10)

        # Terminal widget
        self.terminal = TerminalWidget()
        terminal_layout.addWidget(self.terminal, 1)

        # Progress info row
        self.progress_row = QWidget()
        progress_layout = QHBoxLayout(self.progress_row)
        progress_layout.setContentsMargins(0, 0, 0, 0)

        self.progress_label = QLabel("Initializing...")
        self.progress_label.setStyleSheet("font-size: 12px; color: #888888;")
        progress_layout.addWidget(self.progress_label)
        progress_layout.addStretch()

        self.file_count_label = QLabel("")
        self.file_count_label.setStyleSheet("font-size: 12px; color: #3498db; font-weight: bold;")
        progress_layout.addWidget(self.file_count_label)

        terminal_layout.addWidget(self.progress_row)

        # Error action row (hidden by default)
        self.error_actions = QWidget()
        self.error_actions.hide()
        error_layout = QHBoxLayout(self.error_actions)
        error_layout.setContentsMargins(0, 10, 0, 0)
        error_layout.setSpacing(10)

        self.open_log_btn = QPushButton("Open Log File")
        self.open_log_btn.setStyleSheet("""
            QPushButton {
                background-color: transparent;
                color: #aaaaaa;
                border: 1px solid #555555;
                border-radius: 6px;
                padding: 8px 16px;
                font-size: 12px;
            }
            QPushButton:hover {
                background-color: #3a3a3a;
                color: white;
            }
        """)
        self.open_log_btn.clicked.connect(self.open_log_file)
        error_layout.addWidget(self.open_log_btn)

        self.retry_btn = QPushButton("Retry")
        self.retry_btn.setStyleSheet("""
            QPushButton {
                background-color: #1f6aa5;
                color: white;
                border: none;
                border-radius: 6px;
                padding: 8px 16px;
                font-size: 12px;
            }
            QPushButton:hover {
                background-color: #2980b9;
            }
        """)
        self.retry_btn.clicked.connect(self.retry_generate)
        error_layout.addWidget(self.retry_btn)

        error_layout.addStretch()
        terminal_layout.addWidget(self.error_actions)

        self.stack.addWidget(self.terminal_view)

    def set_paths(self, toolkit_path: str, ram_gb: int = 8, local_java_path: str = None):
        """Set the paths and settings needed for Javadocs generation."""
        self._toolkit_path = toolkit_path
        self._ram_gb = ram_gb
        self._local_java_path = local_java_path

        # Check for existing javadocs
        if toolkit_path:
            javadocs_dir = Path(toolkit_path) / "javadocs"
            index_html = javadocs_dir / "index.html"
            if javadocs_dir.exists() and index_html.exists():
                self._has_existing = True
                self._use_existing = True
                self.existing_banner.show()
                self.javadocs_options.hide()
                self.note.hide()
            else:
                # Fresh install - show toggle so user can choose to skip
                self._has_existing = False
                self.existing_banner.hide()
                self.javadocs_options.show()
                self.javadocs_toggle_row.show()
                self.note.show()

        # Update button state
        if self._button_callback:
            self._button_callback()

    def _set_use_existing(self, use_existing: bool):
        """Toggle between using existing javadocs or regenerating."""
        self._use_existing = use_existing

        # Update button visual states
        self.use_existing_btn.setChecked(use_existing)
        self.regenerate_btn.setChecked(not use_existing)

        # Update button styles
        if use_existing:
            self.use_existing_btn.setStyleSheet("""
                QPushButton {
                    background-color: #1f6aa5;
                    color: white;
                    border: 2px solid #3498db;
                    border-radius: 6px;
                    padding: 0px 20px;
                    font-size: 13px;
                    font-weight: bold;
                }
                QPushButton:hover {
                    background-color: #2980b9;
                    border-color: #5dade2;
                }
            """)
            self.regenerate_btn.setStyleSheet("""
                QPushButton {
                    background-color: transparent;
                    color: #aaaaaa;
                    border: 2px solid #555555;
                    border-radius: 6px;
                    padding: 0px 20px;
                    font-size: 13px;
                    font-weight: bold;
                }
                QPushButton:hover {
                    background-color: #3a3a3a;
                    color: white;
                    border-color: #666666;
                }
            """)
            # Hide generate options when using existing
            self.javadocs_options.hide()
            self.note.hide()
        else:
            self.use_existing_btn.setStyleSheet("""
                QPushButton {
                    background-color: transparent;
                    color: #aaaaaa;
                    border: 2px solid #555555;
                    border-radius: 6px;
                    padding: 0px 20px;
                    font-size: 13px;
                    font-weight: bold;
                }
                QPushButton:hover {
                    background-color: #3a3a3a;
                    color: white;
                    border-color: #666666;
                }
            """)
            self.regenerate_btn.setStyleSheet("""
                QPushButton {
                    background-color: #c0392b;
                    color: white;
                    border: 2px solid #e74c3c;
                    border-radius: 6px;
                    padding: 0px 20px;
                    font-size: 13px;
                    font-weight: bold;
                }
                QPushButton:hover {
                    background-color: #e74c3c;
                    border-color: #ec7063;
                }
            """)
            # Show private members option when regenerating (no toggle needed - they chose to regenerate)
            self.javadocs_options.show()
            self.javadocs_toggle_row.hide()  # Hide toggle - regenerate implies they want javadocs
            self.options_section.show()
            self.options_section.setEnabled(True)
            self.options_opacity.setOpacity(1.0)
            self.note.show()
            self.javadocs_toggle.setChecked(True)

        # Notify wizard to update button text
        if self._button_callback:
            self._button_callback()

    def toggle_options(self, enabled: bool):
        """Enable/disable options based on toggle."""
        self.options_section.setEnabled(enabled)
        self.options_opacity.setOpacity(1.0 if enabled else 0.4)
        # Notify wizard to update button text
        if self._button_callback:
            self._button_callback()

    def set_button_callback(self, callback):
        """Set callback to notify wizard of button changes."""
        self._button_callback = callback

    def set_back_button_callback(self, callback):
        """Set callback for back button changes."""
        self._back_button_callback = callback

    def get_state(self) -> str:
        """Get current state."""
        return self._state

    def get_next_button_config(self) -> dict:
        """Return config for the next button based on current state."""
        if self._state == "running":
            return {
                "text": "Running...",
                "style": "disabled",
                "enabled": False,
            }
        elif self._state == "completed":
            return {
                "text": "Next",
                "style": "primary",
                "enabled": True,
            }
        elif self._state == "failed":
            return {
                "text": "Next",
                "style": "primary",
                "enabled": True,  # Allow proceeding even on failure
            }
        else:  # idle
            # If using existing javadocs, just navigate
            if self._has_existing and self._use_existing:
                return {
                    "text": "Next",
                    "style": "primary",
                    "enabled": True,
                }
            elif self.javadocs_toggle.isChecked():
                return {
                    "text": "Generate",
                    "style": "action",
                    "enabled": True,
                }
            else:
                return {
                    "text": "Skip",
                    "style": "secondary",
                    "enabled": True,
                }

    def get_back_button_config(self) -> dict:
        """Return config for the back button based on current state."""
        if self._state == "running":
            return {
                "text": "Cancel",
                "style": "danger",
                "enabled": True,
            }
        else:
            return {
                "text": "Back",
                "style": "default",
                "enabled": True,
            }

    def should_run_action(self) -> bool:
        """Check if clicking Next should run an action instead of navigating."""
        # Don't run action if using existing javadocs
        if self._has_existing and self._use_existing:
            return False
        return self._state == "idle" and self.javadocs_toggle.isChecked()

    def start_generate(self):
        """Start the Javadocs generation process."""
        if self._state == "running":
            return

        self._state = "running"
        self._file_count = 0
        self._generated_count = 0
        self._in_generating_phase = False

        # Switch to terminal view
        self.stack.setCurrentIndex(1)
        self.desc.setText("Generating Javadocs from decompiled source...")
        self.terminal.clear_terminal()
        self.error_actions.hide()
        self.progress_label.setText("Starting...")
        self.progress_label.setStyleSheet("font-size: 12px; color: #888888;")
        self.file_count_label.setText("")

        # Notify wizard
        if self._button_callback:
            self._button_callback()
        if self._back_button_callback:
            self._back_button_callback()

        # Setup log file
        toolkit_path = Path(self._toolkit_path)
        logs_dir = toolkit_path / "logs"
        logs_dir.mkdir(parents=True, exist_ok=True)
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        self._log_file_path = logs_dir / f"javadocs_{timestamp}.log"

        # Log header
        self.terminal.append_info("=" * 60)
        self.terminal.append_info("Hytale Toolkit - Javadocs Generation Log")
        self.terminal.append_info(f"Started: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        self.terminal.append_info("=" * 60)
        self.terminal.append_line("")

        # Log settings
        include_private = self.private_toggle.isChecked()
        self.terminal.append_line(f"Toolkit Path: {self._toolkit_path}")
        self.terminal.append_line(f"RAM Allocation: {self._ram_gb} GB")
        self.terminal.append_line(f"Include Private: {include_private}")
        self.terminal.append_line("")
        self.terminal.append_warning("Note: Javadoc processes 15,000+ files silently at first.")
        self.terminal.append_warning("This may take 5-10 minutes. Let it cook!")
        self.terminal.append_line("")

        # Verify paths
        decompiled_dir = toolkit_path / "decompiled"
        javadocs_dir = toolkit_path / "javadocs"
        hytale_src = decompiled_dir / "com" / "hypixel"

        if not decompiled_dir.exists():
            self.terminal.append_error("ERROR: No decompiled code found.")
            self.terminal.append_error("Please run decompilation first.")
            self._finish_with_error("Decompiled code not found")
            return

        if not hytale_src.exists():
            self.terminal.append_error(f"ERROR: Hytale source not found at {hytale_src}")
            self._finish_with_error("Hytale source not found")
            return

        # Clear existing javadocs directory if exists
        if javadocs_dir.exists():
            self.terminal.append_warning("Removing existing Javadocs...")
            shutil.rmtree(javadocs_dir)

        javadocs_dir.mkdir(parents=True, exist_ok=True)

        # Fix decompilation artifacts before generating javadocs
        self.terminal.append_line("")
        fix_decompiled_files(decompiled_dir, self.terminal)
        self.terminal.append_line("")

        # Find Java files (excluding package-info.java)
        java_files = [f for f in hytale_src.rglob("*.java") if f.name != "package-info.java"]
        self._total_files = len(java_files)
        self.terminal.append_line(f"Found {self._total_files} Hytale Java files")
        self.terminal.append_line("")

        # Create argfile for javadoc (avoid command line length limits)
        argfile = toolkit_path / ".javadoc-files.txt"
        with open(argfile, "w") as f:
            for java_file in java_files:
                path_str = str(java_file).replace("\\", "/")
                f.write(f'"{path_str}"\n')

        self.terminal.append_info("Starting javadoc generation...")
        self.terminal.append_line("")

        # Start the javadoc process
        self._process = QProcess(self)
        self._process.setProcessChannelMode(QProcess.ProcessChannelMode.MergedChannels)
        self._process.readyReadStandardOutput.connect(self._handle_output)
        self._process.finished.connect(self._handle_finished)
        self._process.errorOccurred.connect(self._handle_error)

        # Build command
        args = [
            f"-J-Xms2G",
            f"-J-Xmx{self._ram_gb}G",
            "-d", str(javadocs_dir),
            "-Xdoclint:none",
            "--ignore-source-errors",
            "-verbose",  # Show progress
        ]

        if include_private:
            args.append("-private")

        args.append(f"@{argfile}")

        # Start heartbeat timer to show activity
        self._heartbeat_count = 0
        self._status_text = "Processing..."  # Base status text
        self._heartbeat_timer = QTimer(self)
        self._heartbeat_timer.timeout.connect(self._show_heartbeat)
        self._heartbeat_timer.start(1000)  # Every 1 second

        # Show immediate feedback
        self.progress_label.setText("Processing... (0s)")

        # Use local JDK if available, otherwise system javadoc
        if self._local_java_path:
            # Get javadoc from the same directory as java
            java_bin_dir = Path(self._local_java_path).parent
            if sys.platform == "win32":
                javadoc_cmd = str(java_bin_dir / "javadoc.exe")
            else:
                javadoc_cmd = str(java_bin_dir / "javadoc")
        else:
            javadoc_cmd = "javadoc"

        self._process.start(javadoc_cmd, args)

    def _set_status(self, text: str):
        """Set the status text (heartbeat will append elapsed time)."""
        self._status_text = text
        self._update_progress_label()

    def _update_progress_label(self):
        """Update progress label with status and elapsed time."""
        elapsed = self._heartbeat_count
        mins = elapsed // 60
        secs = elapsed % 60
        if mins > 0:
            self.progress_label.setText(f"{self._status_text} ({mins}m {secs}s)")
        else:
            self.progress_label.setText(f"{self._status_text} ({secs}s)")

    def _show_heartbeat(self):
        """Show that the process is still running."""
        if self._state != "running":
            if self._heartbeat_timer:
                self._heartbeat_timer.stop()
            return
        self._heartbeat_count += 1
        self._update_progress_label()

    def cancel_generate(self):
        """Cancel the running generation."""
        if self._process and self._state == "running":
            # Stop heartbeat timer
            if hasattr(self, '_heartbeat_timer') and self._heartbeat_timer:
                self._heartbeat_timer.stop()
                self._heartbeat_timer = None

            self.terminal.append_warning("\nCancelling Javadocs generation...")
            self._process.kill()
            self._state = "idle"
            self.stack.setCurrentIndex(0)
            self.desc.setText(
                "Generate browsable HTML documentation of all classes and methods.\n"
                "Useful for exploring the Hytale codebase in a browser."
            )
            if self._button_callback:
                self._button_callback()
            if self._back_button_callback:
                self._back_button_callback()

    def _handle_output(self):
        """Handle process output."""
        if not self._process:
            return

        data = self._process.readAllStandardOutput().data().decode('utf-8', errors='replace')
        for line in data.splitlines():
            line = line.strip()
            if not line:
                continue

            # Parse progress - Loading is 0-50%, Generating is 50-99%
            if "Loading source file" in line:
                self._file_count += 1
                parts = line.split("Loading source file")[-1].strip()
                class_name = parts.split("/")[-1].replace(".java...", "")
                # Loading phase is 0-50%
                percent = (self._file_count * 50) // max(self._total_files, 1)
                self._set_status(f"Loading: {class_name[:40]}")
                self.file_count_label.setText(f"{percent}%")

                # Log every 50th file
                if self._file_count % 50 == 0:
                    self.terminal.append_line(f"[{percent}%] {class_name}")
            elif "Generating" in line:
                # Track generating phase
                if not self._in_generating_phase:
                    self._in_generating_phase = True
                    self.terminal.append_line("")
                    self.terminal.append_info("Generating HTML documentation...")

                self._generated_count += 1
                gen_file = line.split("/")[-1].replace("...", "") if "/" in line else "docs"
                self._set_status(f"Generating: {gen_file[:40]}")

                # Generating phase is 50-99% (estimate total gen files ~ total source files)
                gen_percent = 50 + (self._generated_count * 49) // max(self._total_files, 1)
                gen_percent = min(gen_percent, 99)  # Cap at 99% until truly complete
                self.file_count_label.setText(f"{gen_percent}%")
            elif "error:" in line.lower() and "warning" not in line.lower():
                # Suppress known third-party errors
                suppressed = ["io.sentry", "io.netty", "it.unimi.dsi", "javax.annotation",
                             "com.google.common", "org.bson", "ch.randelshofer", "joptsimple",
                             "org.bouncycastle", "com.github.luben", "com.nimbusds", "org.slf4j",
                             "cannot find symbol", "does not exist"]
                if not any(s in line for s in suppressed):
                    self.terminal.append_error(line)
            elif "warning" in line.lower():
                pass  # Suppress warnings
            else:
                self.terminal.append_line(line)

    def _handle_finished(self, exit_code, exit_status):
        """Handle process completion."""
        # Stop heartbeat timer
        if hasattr(self, '_heartbeat_timer') and self._heartbeat_timer:
            self._heartbeat_timer.stop()
            self._heartbeat_timer = None

        self.terminal.append_line("")

        # Check if javadocs were actually generated
        toolkit_path = Path(self._toolkit_path)
        javadocs_dir = toolkit_path / "javadocs"
        index_html = javadocs_dir / "index.html"

        if index_html.exists():
            self._state = "completed"
            generated_files = list(javadocs_dir.glob("**/*.html"))
            self.terminal.append_success("=" * 60)
            self.terminal.append_success("Javadocs generated successfully!")
            self.terminal.append_success(f"Generated {len(generated_files)} HTML files")
            self.terminal.append_success("=" * 60)

            self.desc.setText("Javadocs generated successfully!")
            self.progress_label.setText("Complete!")
            self.progress_label.setStyleSheet("font-size: 12px; color: #22C55E; font-weight: bold;")
            self.file_count_label.setText(f"{len(generated_files)} files")
        else:
            self._finish_with_error(f"Generation failed - no output produced (exit code {exit_code})")
            return

        # Save log file
        self._save_log()

        # Clean up argfile
        argfile = toolkit_path / ".javadoc-files.txt"
        if argfile.exists():
            argfile.unlink()

        # Notify wizard
        if self._button_callback:
            self._button_callback()
        if self._back_button_callback:
            self._back_button_callback()

    def _handle_error(self, error):
        """Handle process error."""
        error_messages = {
            QProcess.ProcessError.FailedToStart: "Failed to start javadoc. Is JDK installed?",
            QProcess.ProcessError.Crashed: "Process crashed unexpectedly",
            QProcess.ProcessError.Timedout: "Process timed out",
            QProcess.ProcessError.WriteError: "Write error",
            QProcess.ProcessError.ReadError: "Read error",
            QProcess.ProcessError.UnknownError: "Unknown error",
        }
        msg = error_messages.get(error, f"Error: {error}")
        self.terminal.append_error(f"\nERROR: {msg}")
        self._finish_with_error(msg)

    def _finish_with_error(self, error_msg: str):
        """Handle generation failure."""
        self._state = "failed"
        self.terminal.append_line("")
        self.terminal.append_error("=" * 60)
        self.terminal.append_error(f"Javadocs generation failed: {error_msg}")
        self.terminal.append_error("=" * 60)

        self.desc.setText("Generation failed. You can retry or continue without it.")
        self.progress_label.setText("Failed")
        self.progress_label.setStyleSheet("font-size: 12px; color: #EF4444; font-weight: bold;")
        self.error_actions.show()

        self._save_log()

        if self._button_callback:
            self._button_callback()
        if self._back_button_callback:
            self._back_button_callback()

    def _save_log(self):
        """Save the log to file."""
        if self._log_file_path:
            try:
                with open(self._log_file_path, 'w', encoding='utf-8') as f:
                    f.write(self.terminal.get_full_log())
            except Exception as e:
                self.terminal.append_warning(f"Failed to save log: {e}")

    def copy_log_to_clipboard(self):
        """Copy the full log to clipboard."""
        clipboard = QApplication.clipboard()
        clipboard.setText(self.terminal.get_full_log())
        self.copy_log_btn.setText("Copied!")

    def open_log_file(self):
        """Open the log file in the default text editor."""
        if self._log_file_path and self._log_file_path.exists():
            if sys.platform == "win32":
                os.startfile(str(self._log_file_path))
            elif sys.platform == "darwin":
                import subprocess
                subprocess.run(["open", str(self._log_file_path)])
            else:
                import subprocess
                subprocess.run(["xdg-open", str(self._log_file_path)])

    def retry_generate(self):
        """Retry the generation."""
        self._state = "idle"
        self.progress_label.setStyleSheet("font-size: 12px; color: #888888;")
        self.start_generate()

    def get_settings(self) -> dict:
        """Return the Javadocs settings."""
        return {
            "enabled": self.javadocs_toggle.isChecked(),
            "include_private": self.private_toggle.isChecked() if self.javadocs_toggle.isChecked() else False,
            "completed": self._state == "completed",
        }


class ProviderCard(QFrame):
    """A clickable provider selection card."""

    clicked = None  # Signal placeholder

    def __init__(self, name: str, badge: str, features: list, recommended: bool, parent=None):
        super().__init__(parent)
        self._selected = False
        self._name = name
        self.setCursor(Qt.CursorShape.PointingHandCursor)
        self.setFixedSize(200, 195)

        self._setup_ui(name, badge, features, recommended)
        self._update_style()

    def _setup_ui(self, name: str, badge: str, features: list, recommended: bool):
        layout = QVBoxLayout(self)
        layout.setContentsMargins(18, 15, 18, 15)
        layout.setSpacing(6)

        # Header
        header = QHBoxLayout()
        self.name_label = QLabel(name)
        self.name_label.setStyleSheet("font-size: 15px; font-weight: bold; color: white; background: transparent;")
        header.addWidget(self.name_label)
        header.addStretch()

        # Badge
        self.badge_label = QLabel(badge)
        self._badge_color = "#3498db" if badge == "Cloud" else "#F59E0B"
        header.addWidget(self.badge_label)
        layout.addLayout(header)

        # Recommended tag
        self.rec_label = None
        if recommended:
            self.rec_label = QLabel("Recommended")
            self.rec_label.setStyleSheet("font-size: 10px; color: #22C55E; font-weight: bold; background: transparent;")
            layout.addWidget(self.rec_label)
        else:
            layout.addSpacing(14)

        layout.addSpacing(6)

        # Features list as bullet points
        self.feature_labels = []
        for feature in features:
            feat_label = QLabel(f"•  {feature}")
            feat_label.setStyleSheet("font-size: 11px; color: #999999; background: transparent;")
            layout.addWidget(feat_label)
            self.feature_labels.append(feat_label)

        layout.addStretch()

    def setSelected(self, selected: bool):
        self._selected = selected
        self._update_style()

    def isSelected(self) -> bool:
        return self._selected

    def _update_style(self):
        if self._selected:
            self.setStyleSheet("""
                ProviderCard {
                    background-color: #2a2a2a;
                    border: 2px solid #3498db;
                    border-radius: 10px;
                }
            """)
            self.badge_label.setStyleSheet(
                f"font-size: 10px; color: {self._badge_color}; background-color: {self._badge_color}25; "
                f"padding: 3px 8px; border-radius: 4px; font-weight: bold;"
            )
            self.name_label.setStyleSheet("font-size: 15px; font-weight: bold; color: white; background: transparent;")
            if self.rec_label:
                self.rec_label.setStyleSheet("font-size: 10px; color: #22C55E; font-weight: bold; background: transparent;")
            for label in self.feature_labels:
                label.setStyleSheet("font-size: 11px; color: #999999; background: transparent;")
        else:
            self.setStyleSheet("""
                ProviderCard {
                    background-color: #1e1e1e;
                    border: 1px solid #333333;
                    border-radius: 10px;
                }
                ProviderCard:hover {
                    background-color: #252525;
                    border-color: #444444;
                }
            """)
            # Keep badge, recommended, and title visible even when card is not selected
            self.badge_label.setStyleSheet(
                f"font-size: 10px; color: {self._badge_color}; background-color: {self._badge_color}20; "
                f"padding: 3px 8px; border-radius: 4px; font-weight: bold;"
            )
            self.name_label.setStyleSheet("font-size: 15px; font-weight: bold; color: white; background: transparent;")
            if self.rec_label:
                self.rec_label.setStyleSheet("font-size: 10px; color: #22C55E; font-weight: bold; background: transparent;")
            for label in self.feature_labels:
                label.setStyleSheet("font-size: 11px; color: #555555; background: transparent;")

    def enterEvent(self, event):
        if not self._selected:
            self.setStyleSheet("""
                ProviderCard {
                    background-color: #252525;
                    border: 1px solid #444444;
                    border-radius: 10px;
                }
            """)
        super().enterEvent(event)

    def leaveEvent(self, event):
        self._update_style()
        super().leaveEvent(event)

    def mousePressEvent(self, _event):
        if hasattr(self, '_click_callback') and self._click_callback:
            self._click_callback(self)

    def set_click_callback(self, callback):
        self._click_callback = callback


class ProviderPage(QWidget):
    """Page for selecting embedding provider (Voyage or Ollama)."""

    def __init__(self, parent=None):
        super().__init__(parent)
        self._selected_provider = "voyage"

        layout = QVBoxLayout(self)
        layout.setContentsMargins(40, 30, 40, 20)

        # Title
        title = QLabel("Choose Embedding Provider")
        title.setStyleSheet("font-size: 22px; font-weight: bold; color: white;")
        title.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(title)

        # Description
        desc = QLabel(
            "Select how you want to generate embeddings for semantic code search."
        )
        desc.setStyleSheet("color: #aaaaaa; font-size: 13px;")
        desc.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(desc)

        layout.addSpacing(20)

        # Provider cards container
        cards_container = QWidget()
        cards_layout = QHBoxLayout(cards_container)
        cards_layout.setSpacing(20)
        cards_layout.setContentsMargins(0, 0, 0, 0)

        # Voyage card
        self.voyage_card = ProviderCard(
            "Voyage AI",
            "Cloud",
            [
                "Best quality embeddings",
                "Fast cloud processing",
                "No local resources needed",
                "Free for 200M tokens",
            ],
            recommended=True,
        )
        self.voyage_card.setSelected(True)
        self.voyage_card.set_click_callback(self.on_card_clicked)
        cards_layout.addWidget(self.voyage_card)

        # Ollama card
        self.ollama_card = ProviderCard(
            "Ollama",
            "Local",
            [
                "Runs entirely offline",
                "Completely free forever",
                "No account required",
                "Requires ~4GB RAM",
            ],
            recommended=False,
        )
        self.ollama_card.setSelected(False)
        self.ollama_card.set_click_callback(self.on_card_clicked)
        cards_layout.addWidget(self.ollama_card)

        # Center the cards
        center_container = QWidget()
        center_layout = QHBoxLayout(center_container)
        center_layout.setContentsMargins(0, 0, 0, 0)
        center_layout.addStretch()
        center_layout.addWidget(cards_container)
        center_layout.addStretch()
        layout.addWidget(center_container)

        layout.addSpacing(15)

        # API key section (shown only for Voyage)
        self.api_key_section = QWidget()
        api_key_layout = QVBoxLayout(self.api_key_section)
        api_key_layout.setContentsMargins(0, 0, 0, 0)
        api_key_layout.setSpacing(10)

        api_key_label = QLabel("Voyage API Key")
        api_key_label.setStyleSheet("font-size: 14px; font-weight: bold; color: white;")
        api_key_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        api_key_layout.addWidget(api_key_label)

        # Center the input
        input_container = QWidget()
        input_layout = QHBoxLayout(input_container)
        input_layout.setContentsMargins(0, 0, 0, 0)
        input_layout.addStretch()

        self.api_key_input = QLineEdit()
        self.api_key_input.setPlaceholderText("Enter your Voyage API key...")
        self.api_key_input.setEchoMode(QLineEdit.EchoMode.Password)
        self.api_key_input.setFixedWidth(350)
        self.api_key_input.setStyleSheet("""
            QLineEdit {
                background-color: #1e1e1e;
                border: 1px solid #3a3a3a;
                border-radius: 6px;
                padding: 12px 15px;
                font-size: 13px;
                color: white;
            }
            QLineEdit:focus {
                border-color: #3498db;
            }
        """)
        input_layout.addWidget(self.api_key_input)
        input_layout.addStretch()
        api_key_layout.addWidget(input_container)

        # Disclaimer about payment
        disclaimer = QLabel(
            "Voyage requires adding a payment method, but won't charge until you\n"
            "exceed 200M tokens — more than enough for this toolkit."
        )
        disclaimer.setStyleSheet("font-size: 11px; color: #888888;")
        disclaimer.setAlignment(Qt.AlignmentFlag.AlignCenter)
        api_key_layout.addWidget(disclaimer)

        # Clickable link
        api_key_link = QLabel('<a href="https://dash.voyageai.com/api-keys" style="color: #3498db;">Get your API key at voyage.ai</a>')
        api_key_link.setStyleSheet("font-size: 11px;")
        api_key_link.setAlignment(Qt.AlignmentFlag.AlignCenter)
        api_key_link.setOpenExternalLinks(True)
        api_key_layout.addWidget(api_key_link)

        layout.addWidget(self.api_key_section)

        layout.addStretch()

        self._toolkit_path = None
        self._api_key_loaded = False

    def set_toolkit_path(self, toolkit_path: str):
        """Set the toolkit path and load API key from .env if available."""
        self._toolkit_path = toolkit_path

        # Only auto-load once to avoid overwriting user input
        if not self._api_key_loaded:
            existing_key = load_env_api_key(toolkit_path)
            if existing_key:
                self.api_key_input.setText(existing_key)
            self._api_key_loaded = True

    def on_card_clicked(self, card):
        """Handle card selection."""
        if card == self.voyage_card:
            self._selected_provider = "voyage"
            self.voyage_card.setSelected(True)
            self.ollama_card.setSelected(False)
            self.api_key_section.setVisible(True)
        else:
            self._selected_provider = "ollama"
            self.voyage_card.setSelected(False)
            self.ollama_card.setSelected(True)
            self.api_key_section.setVisible(False)

    def get_settings(self) -> dict:
        """Return the provider settings."""
        return {
            "provider": self._selected_provider,
            "api_key": self.api_key_input.text().strip()
            if self._selected_provider == "voyage"
            else "",
        }


class DatabasePage(QWidget):
    """Page for database setup with terminal output."""

    # Signals for state changes
    state_changed = pyqtSignal(str)  # idle, running, completed, failed

    def __init__(self, parent=None):
        super().__init__(parent)
        self._button_callback = None
        self._back_button_callback = None
        self._state = "idle"  # idle, running, completed, failed
        self._process = None
        self._toolkit_path = None
        self._provider = None
        self._log_file_path = None
        self._has_existing = False
        self._use_existing = True

        layout = QVBoxLayout(self)
        layout.setContentsMargins(40, 40, 40, 30)

        # Title
        self.title = QLabel("Download Search Database")
        self.title.setStyleSheet("font-size: 22px; font-weight: bold; color: white;")
        self.title.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(self.title)

        # Description
        self.desc = QLabel(
            "Download the pre-built vector database for semantic code search.\n"
            "This contains indexed embeddings for fast Hytale code lookup."
        )
        self.desc.setStyleSheet("color: #aaaaaa; font-size: 13px;")
        self.desc.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.desc.setWordWrap(True)
        layout.addWidget(self.desc)

        layout.addSpacing(20)

        # Stacked widget for settings/terminal views
        self.stack = QStackedWidget()
        layout.addWidget(self.stack, 1)

        # ===== Settings View (index 0) =====
        self.settings_view = QWidget()
        settings_layout = QVBoxLayout(self.settings_view)
        settings_layout.setContentsMargins(0, 10, 0, 0)
        settings_layout.setSpacing(15)

        # ===== Main Card =====
        self.main_card = QFrame()
        self.main_card.setFixedWidth(240)
        self.main_card.setStyleSheet("""
            QFrame#dbCard {
                background-color: #2a2a2a;
                border: 2px solid #3498db;
                border-radius: 10px;
            }
        """)
        self.main_card.setObjectName("dbCard")
        card_layout = QVBoxLayout(self.main_card)
        card_layout.setContentsMargins(18, 15, 18, 15)
        card_layout.setSpacing(6)

        # Header row with title and badge
        header = QHBoxLayout()
        card_title = QLabel("Vector Database")
        card_title.setStyleSheet("font-size: 15px; font-weight: bold; color: white; background: transparent;")
        header.addWidget(card_title)
        header.addStretch()

        self.provider_badge = QLabel("Voyage")
        self.provider_badge.setStyleSheet(
            "font-size: 10px; color: #3498db; background-color: #3498db25; "
            "padding: 3px 8px; border-radius: 4px; font-weight: bold;"
        )
        header.addWidget(self.provider_badge)
        card_layout.addLayout(header)

        # Size subtitle
        size_label = QLabel("~50-100 MB download")
        size_label.setStyleSheet("font-size: 11px; color: #22C55E; font-weight: bold; background: transparent;")
        card_layout.addWidget(size_label)

        card_layout.addSpacing(8)

        # Feature list
        features = [
            "Pre-indexed code embeddings",
            "Enables semantic search",
            "One-time download",
            "Required for code lookup",
        ]
        for feature in features:
            feat_label = QLabel(f"•  {feature}")
            feat_label.setStyleSheet("font-size: 11px; color: #999999; background: transparent;")
            card_layout.addWidget(feat_label)

        # Center the card
        card_center = QWidget()
        card_center_layout = QHBoxLayout(card_center)
        card_center_layout.setContentsMargins(0, 0, 0, 0)
        card_center_layout.addStretch()
        card_center_layout.addWidget(self.main_card)
        card_center_layout.addStretch()
        settings_layout.addWidget(card_center)

        # ===== Existing Database Options (hidden by default) =====
        self.existing_options = QWidget()
        self.existing_options.hide()
        existing_layout = QVBoxLayout(self.existing_options)
        existing_layout.setContentsMargins(0, 0, 0, 0)
        existing_layout.setSpacing(10)

        existing_label = QLabel("✓ Existing database found")
        existing_label.setStyleSheet("font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 13px; font-weight: bold; color: #22C55E;")
        existing_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        existing_layout.addWidget(existing_label)

        # Option buttons
        options_container = QWidget()
        options_btn_layout = QHBoxLayout(options_container)
        options_btn_layout.setContentsMargins(0, 0, 0, 0)
        options_btn_layout.setSpacing(10)
        options_btn_layout.addStretch()

        self.use_existing_btn = QPushButton("Use Existing")
        self.use_existing_btn.setCheckable(True)
        self.use_existing_btn.setChecked(True)
        self.use_existing_btn.setFixedHeight(36)
        self.use_existing_btn.setStyleSheet("""
            QPushButton {
                background-color: #1f6aa5;
                color: white;
                border: 2px solid #1f6aa5;
                border-radius: 6px;
                padding: 0px 16px;
                font-size: 12px;
                font-weight: bold;
            }
            QPushButton:hover {
                background-color: #2980b9;
                border-color: #2980b9;
            }
        """)
        self.use_existing_btn.clicked.connect(lambda: self._set_use_existing(True))
        options_btn_layout.addWidget(self.use_existing_btn)

        self.redownload_btn = QPushButton("Re-download")
        self.redownload_btn.setCheckable(True)
        self.redownload_btn.setFixedHeight(36)
        self.redownload_btn.setStyleSheet("""
            QPushButton {
                background-color: transparent;
                color: #aaaaaa;
                border: 2px solid #555555;
                border-radius: 6px;
                padding: 0px 16px;
                font-size: 12px;
                font-weight: bold;
            }
            QPushButton:hover {
                background-color: #3a3a3a;
                color: white;
                border-color: #666666;
            }
        """)
        self.redownload_btn.clicked.connect(lambda: self._set_use_existing(False))
        options_btn_layout.addWidget(self.redownload_btn)

        options_btn_layout.addStretch()
        existing_layout.addWidget(options_container)
        settings_layout.addWidget(self.existing_options)

        settings_layout.addStretch()

        self.stack.addWidget(self.settings_view)

        # ===== Terminal View (index 1) =====
        self.terminal_view = QWidget()
        terminal_layout = QVBoxLayout(self.terminal_view)
        terminal_layout.setContentsMargins(0, 0, 0, 0)
        terminal_layout.setSpacing(10)

        self.terminal = TerminalWidget()
        terminal_layout.addWidget(self.terminal, 1)

        # Progress info row
        self.progress_row = QWidget()
        progress_layout = QHBoxLayout(self.progress_row)
        progress_layout.setContentsMargins(0, 0, 0, 0)

        self.progress_label = QLabel("Initializing...")
        self.progress_label.setStyleSheet("font-size: 12px; color: #888888;")
        self.progress_label.setMinimumHeight(18)
        progress_layout.addWidget(self.progress_label)
        progress_layout.addStretch()

        terminal_layout.addWidget(self.progress_row)

        # Error action row
        self.error_actions = QWidget()
        self.error_actions.hide()
        error_layout = QHBoxLayout(self.error_actions)
        error_layout.setContentsMargins(0, 10, 0, 0)
        error_layout.setSpacing(10)

        self.open_log_btn = QPushButton("Open Log File")
        self.open_log_btn.setStyleSheet("""
            QPushButton {
                background-color: transparent;
                color: #aaaaaa;
                border: 1px solid #555555;
                border-radius: 6px;
                padding: 8px 16px;
                font-size: 12px;
            }
            QPushButton:hover {
                background-color: #3a3a3a;
                color: white;
            }
        """)
        self.open_log_btn.clicked.connect(self.open_log_file)
        error_layout.addWidget(self.open_log_btn)

        self.retry_btn = QPushButton("Retry")
        self.retry_btn.setStyleSheet("""
            QPushButton {
                background-color: #1f6aa5;
                color: white;
                border: none;
                border-radius: 6px;
                padding: 8px 16px;
                font-size: 12px;
            }
            QPushButton:hover {
                background-color: #2980b9;
            }
        """)
        self.retry_btn.clicked.connect(self.retry_download)
        error_layout.addWidget(self.retry_btn)

        error_layout.addStretch()
        terminal_layout.addWidget(self.error_actions)

        self.stack.addWidget(self.terminal_view)

    def set_paths(self, toolkit_path: str, provider: str):
        """Set the paths needed for database download."""
        self._toolkit_path = toolkit_path
        self._provider = provider

        # Update provider badge
        if provider:
            display_name = "Voyage" if provider == "voyage" else "Ollama"
            badge_color = "#3498db" if provider == "voyage" else "#F59E0B"
            self.provider_badge.setText(display_name)
            self.provider_badge.setStyleSheet(
                f"font-size: 10px; color: {badge_color}; background-color: {badge_color}25; "
                f"padding: 3px 8px; border-radius: 4px; font-weight: bold;"
            )

        # Check for existing database
        if toolkit_path and provider:
            lancedb_dir = Path(toolkit_path) / "hytale-rag" / "data" / provider / "lancedb"
            tables = ["hytale_methods.lance", "hytale_client_ui.lance", "hytale_gamedata.lance"]
            if lancedb_dir.exists() and all((lancedb_dir / t).exists() for t in tables):
                self._has_existing = True
                self._use_existing = True
                self.existing_options.show()
            else:
                self._has_existing = False
                self.existing_options.hide()

        if self._button_callback:
            self._button_callback()

    def _set_use_existing(self, use_existing: bool):
        """Toggle between using existing or re-downloading."""
        self._use_existing = use_existing
        self.use_existing_btn.setChecked(use_existing)
        self.redownload_btn.setChecked(not use_existing)

        if use_existing:
            self.use_existing_btn.setStyleSheet("""
                QPushButton {
                    background-color: #1f6aa5;
                    color: white;
                    border: 2px solid #1f6aa5;
                    border-radius: 6px;
                    padding: 0px 16px;
                    font-size: 12px;
                    font-weight: bold;
                }
                QPushButton:hover {
                    background-color: #2980b9;
                    border-color: #2980b9;
                }
            """)
            self.redownload_btn.setStyleSheet("""
                QPushButton {
                    background-color: transparent;
                    color: #aaaaaa;
                    border: 2px solid #555555;
                    border-radius: 6px;
                    padding: 0px 16px;
                    font-size: 12px;
                    font-weight: bold;
                }
                QPushButton:hover {
                    background-color: #3a3a3a;
                    color: white;
                    border-color: #666666;
                }
            """)
        else:
            self.use_existing_btn.setStyleSheet("""
                QPushButton {
                    background-color: transparent;
                    color: #aaaaaa;
                    border: 2px solid #555555;
                    border-radius: 6px;
                    padding: 0px 16px;
                    font-size: 12px;
                    font-weight: bold;
                }
                QPushButton:hover {
                    background-color: #3a3a3a;
                    color: white;
                    border-color: #666666;
                }
            """)
            self.redownload_btn.setStyleSheet("""
                QPushButton {
                    background-color: #c0392b;
                    color: white;
                    border: 2px solid #e74c3c;
                    border-radius: 6px;
                    padding: 0px 16px;
                    font-size: 12px;
                    font-weight: bold;
                }
                QPushButton:hover {
                    background-color: #e74c3c;
                }
            """)

        if self._button_callback:
            self._button_callback()

    def set_button_callback(self, callback):
        self._button_callback = callback

    def set_back_button_callback(self, callback):
        self._back_button_callback = callback

    def get_state(self) -> str:
        return self._state

    def get_next_button_config(self) -> dict:
        if self._state == "running":
            return {"text": "Downloading...", "style": "disabled", "enabled": False}
        elif self._state == "completed":
            return {"text": "Next", "style": "primary", "enabled": True}
        elif self._state == "failed":
            return {"text": "Next", "style": "primary", "enabled": True}
        else:  # idle
            if self._has_existing and self._use_existing:
                return {"text": "Next", "style": "primary", "enabled": True}
            else:
                return {"text": "Download", "style": "action", "enabled": True}

    def get_back_button_config(self) -> dict:
        if self._state == "running":
            return {"text": "Cancel", "style": "danger", "enabled": True}
        else:
            return {"text": "Back", "style": "default", "enabled": True}

    def should_run_action(self) -> bool:
        if self._has_existing and self._use_existing:
            return False
        return self._state == "idle"

    def start_download(self):
        """Start the database download process."""
        if self._state == "running":
            return

        self._state = "running"
        self.stack.setCurrentIndex(1)
        self.desc.setText("Downloading pre-built database...")
        self.terminal.clear_terminal()
        self.error_actions.hide()
        self.progress_label.setText("Starting download...")

        if self._button_callback:
            self._button_callback()
        if self._back_button_callback:
            self._back_button_callback()

        # Setup log file
        toolkit_path = Path(self._toolkit_path)
        logs_dir = toolkit_path / "logs"
        logs_dir.mkdir(parents=True, exist_ok=True)
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        self._log_file_path = logs_dir / f"database_{timestamp}.log"

        # Log header
        self.terminal.append_info("=" * 60)
        self.terminal.append_info("Hytale Toolkit - Database Download Log")
        self.terminal.append_info(f"Started: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        self.terminal.append_info("=" * 60)
        self.terminal.append_line("")
        self.terminal.append_line(f"Provider: {self._provider}")
        self.terminal.append_line(f"Toolkit Path: {self._toolkit_path}")
        self.terminal.append_line("")

        # Run the download script
        provider_dir = toolkit_path / "hytale-rag" / "data" / self._provider
        provider_dir.mkdir(parents=True, exist_ok=True)

        lancedb_dir = provider_dir / "lancedb"
        if lancedb_dir.exists():
            self.terminal.append_warning("Removing existing database...")
            shutil.rmtree(lancedb_dir)

        self.terminal.append_info("Starting download...")
        self.terminal.append_line("")

        # Start Python process to run download
        self._process = QProcess(self)
        self._process.setProcessChannelMode(QProcess.ProcessChannelMode.MergedChannels)
        self._process.readyReadStandardOutput.connect(self._handle_output)
        self._process.finished.connect(self._handle_finished)
        self._process.errorOccurred.connect(self._handle_error)

        # Find Python interpreter (sys.executable is the .exe when frozen)
        if getattr(_sys, '_MEIPASS', None):
            # Running as bundled exe - find system Python
            if sys.platform == "win32":
                python_cmd = "python"
            else:
                python_cmd = "python3"
        else:
            python_cmd = sys.executable

        # Use toolkit path to find setup.py (not the bundle's temp directory)
        hytale_rag_dir = toolkit_path / "hytale-rag"

        # Run the setup.py download_database function via Python
        script = f'''
import sys
sys.path.insert(0, r"{hytale_rag_dir}")
from setup import download_database
from pathlib import Path
success = download_database(Path(r"{provider_dir}"), "{self._provider}")
sys.exit(0 if success else 1)
'''
        self._process.start(python_cmd, ["-c", script])

    def cancel_download(self):
        if self._process and self._state == "running":
            self.terminal.append_warning("\nCancelling download...")
            self._process.kill()
            self._state = "idle"
            self.stack.setCurrentIndex(0)
            self.desc.setText(
                "Download the pre-built vector database for semantic code search.\n"
                "This contains indexed embeddings for fast Hytale code lookup."
            )
            if self._button_callback:
                self._button_callback()
            if self._back_button_callback:
                self._back_button_callback()

    def _handle_output(self):
        if not self._process:
            return
        data = self._process.readAllStandardOutput().data().decode('utf-8', errors='replace')
        for line in data.splitlines():
            line = line.strip()
            if not line:
                continue
            if "error" in line.lower():
                self.terminal.append_error(line)
            elif "warning" in line.lower():
                self.terminal.append_warning(line)
            elif "%" in line or "download" in line.lower():
                self.terminal.append_info(line)
                self.progress_label.setText(line[:50])
            else:
                self.terminal.append_line(line)

    def _handle_finished(self, exit_code, exit_status):
        self.terminal.append_line("")
        if exit_code == 0:
            self._state = "completed"
            self.terminal.append_success("=" * 60)
            self.terminal.append_success("Database download completed successfully!")
            self.terminal.append_success("=" * 60)
            self.desc.setText("Database downloaded successfully!")
            self.progress_label.setText("Complete!")
            self.progress_label.setStyleSheet("font-size: 12px; color: #22C55E; font-weight: bold;")
        else:
            self._finish_with_error(f"Download failed with exit code {exit_code}")

        self._save_log()
        if self._button_callback:
            self._button_callback()
        if self._back_button_callback:
            self._back_button_callback()

    def _handle_error(self, error):
        error_messages = {
            QProcess.ProcessError.FailedToStart: "Failed to start Python",
            QProcess.ProcessError.Crashed: "Process crashed",
            QProcess.ProcessError.Timedout: "Process timed out",
        }
        msg = error_messages.get(error, f"Error: {error}")
        self.terminal.append_error(f"\nERROR: {msg}")
        self._finish_with_error(msg)

    def _finish_with_error(self, error_msg: str):
        self._state = "failed"
        self.terminal.append_line("")
        self.terminal.append_error("=" * 60)
        self.terminal.append_error(f"Download failed: {error_msg}")
        self.terminal.append_error("=" * 60)
        self.desc.setText("Download failed. You can retry or continue without it.")
        self.progress_label.setText("Failed")
        self.progress_label.setStyleSheet("font-size: 12px; color: #EF4444; font-weight: bold;")
        self.error_actions.show()
        self._save_log()
        if self._button_callback:
            self._button_callback()
        if self._back_button_callback:
            self._back_button_callback()

    def _save_log(self):
        if self._log_file_path:
            try:
                with open(self._log_file_path, 'w', encoding='utf-8') as f:
                    f.write(self.terminal.get_full_log())
            except Exception as e:
                self.terminal.append_warning(f"Failed to save log: {e}")

    def open_log_file(self):
        if self._log_file_path and self._log_file_path.exists():
            import subprocess
            if sys.platform == "win32":
                os.startfile(str(self._log_file_path))
            elif sys.platform == "darwin":
                subprocess.run(["open", str(self._log_file_path)])
            else:
                subprocess.run(["xdg-open", str(self._log_file_path)])

    def retry_download(self):
        self._state = "idle"
        self.progress_label.setStyleSheet("font-size: 12px; color: #888888;")
        self.start_download()


def check_node_installed() -> tuple[bool, str]:
    """Check if Node.js is installed. Returns (is_installed, version_or_error)."""
    try:
        result = subprocess.run(
            ["node", "--version"],
            capture_output=True,
            text=True,
            timeout=5,
            shell=(sys.platform == "win32")
        )
        if result.returncode == 0:
            version = result.stdout.strip()
            return True, version
        return False, "Node.js not found"
    except Exception:
        return False, "Node.js not found"


def check_java_installed() -> tuple[bool, str, int]:
    """
    Check if Java is installed and get the version.
    Returns (is_installed, version_string, major_version).
    """
    try:
        result = subprocess.run(
            ["java", "-version"],
            capture_output=True,
            text=True,
            timeout=10,
            shell=(sys.platform == "win32")
        )
        # Java outputs version to stderr
        output = result.stderr if result.stderr else result.stdout
        if result.returncode == 0 and output:
            # Parse version like: openjdk version "21.0.1" or java version "1.8.0_xxx"
            import re
            match = re.search(r'version "(\d+)(?:\.(\d+))?', output)
            if match:
                major = int(match.group(1))
                # Handle old 1.x versioning (1.8 = Java 8)
                if major == 1 and match.group(2):
                    major = int(match.group(2))
                version_line = output.split('\n')[0].strip()
                return True, version_line, major
        return False, "Java not found", 0
    except Exception:
        return False, "Java not found", 0


def get_adoptium_download_info() -> dict:
    """
    Get the Adoptium JDK 25 ZIP download URL for the current platform.
    Returns dict with 'url', 'filename', 'os', 'arch'.
    """
    # Determine OS
    if sys.platform == "win32":
        os_name = "windows"
        ext = "zip"
    elif sys.platform == "darwin":
        os_name = "mac"
        ext = "tar.gz"
    else:
        os_name = "linux"
        ext = "tar.gz"

    # Determine architecture
    import platform
    machine = platform.machine().lower()
    if machine in ("x86_64", "amd64"):
        arch = "x64"
    elif machine in ("aarch64", "arm64"):
        arch = "aarch64"
    else:
        arch = "x64"  # Default to x64

    # Query the Adoptium API to get the ZIP download URL
    # First get asset info, then extract the ZIP/tar.gz package link
    api_url = f"https://api.adoptium.net/v3/assets/latest/25/hotspot?os={os_name}&architecture={arch}&image_type=jdk"

    try:
        req = urllib.request.Request(api_url, headers={"User-Agent": "Hytale-Toolkit"})
        with urllib.request.urlopen(req, timeout=15) as response:
            data = json.loads(response.read().decode())

        # Find the ZIP/tar.gz package (not MSI/PKG)
        for asset in data:
            binary = asset.get("binary", {})
            package = binary.get("package", {})
            link = package.get("link", "")
            name = package.get("name", "")

            # Skip installers, get archive
            if ext == "zip" and name.endswith(".zip"):
                return {
                    "url": link,
                    "filename": f"adoptium-jdk25.{ext}",
                    "os": os_name,
                    "arch": arch,
                }
            elif ext == "tar.gz" and name.endswith(".tar.gz"):
                return {
                    "url": link,
                    "filename": f"adoptium-jdk25.{ext}",
                    "os": os_name,
                    "arch": arch,
                }
    except Exception:
        pass

    # Fallback - construct URL directly (may redirect)
    base_url = "https://api.adoptium.net/v3/binary/latest"
    url = f"{base_url}/25/ea/{os_name}/{arch}/jdk/hotspot/normal/eclipse"

    return {
        "url": url,
        "filename": f"adoptium-jdk25.{ext}",
        "os": os_name,
        "arch": arch,
    }


class IntegrationPage(QWidget):
    """Page for configuring MCP client integrations - three step process.

    Step 0: IDE selection (VS Code, JetBrains, Desktop/CLI)
    Step 1: Provider selection (which AI assistants to configure)
    Step 2: Installation results (shows success/failure for each provider)
    """

    def __init__(self, parent=None):
        super().__init__(parent)
        self._button_callback = None
        self._back_button_callback = None
        self._current_step = 0  # 0 = IDE selection, 1 = Provider selection, 2 = Results
        self._node_installed = False
        self._node_version = ""
        self._toolkit_path = None
        self._install_results = {}  # provider_id -> (success, message)
        self._is_installing = False

        layout = QVBoxLayout(self)
        layout.setContentsMargins(40, 30, 40, 20)

        # Title (changes based on step)
        self.title = QLabel("Select Your IDE")
        self.title.setStyleSheet("font-size: 22px; font-weight: bold; color: white;")
        self.title.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(self.title)

        # Description (changes based on step)
        self.desc = QLabel(
            "Choose your development environment.\n"
            "We'll configure the MCP server for it."
        )
        self.desc.setStyleSheet("color: #aaaaaa; font-size: 13px;")
        self.desc.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.desc.setWordWrap(True)
        layout.addWidget(self.desc)

        layout.addSpacing(8)

        # Stacked widget for the three steps
        self.stack = QStackedWidget()
        layout.addWidget(self.stack)

        # ===== Step 0: IDE Selection =====
        self._setup_ide_selection()

        # ===== Step 1: Provider Selection =====
        self._setup_provider_selection()

        # ===== Step 2: Installation Results =====
        self._setup_results_view()

        layout.addSpacing(12)

        # Node.js status (success or warning) - below cards
        self.node_status = QLabel()
        self.node_status.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.node_status.setOpenExternalLinks(True)
        self.node_status.hide()
        layout.addWidget(self.node_status)

        layout.addSpacing(8)

        # Note at bottom
        self.note = QLabel(
            "You can always change these settings later in the configuration file."
        )
        self.note.setStyleSheet("font-size: 11px; color: #666666;")
        self.note.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(self.note)

        # Check for Node.js
        self._check_node()

        layout.addStretch()

    def _setup_ide_selection(self):
        """Setup the IDE selection view (step 1)."""
        self.ide_view = QWidget()
        ide_layout = QVBoxLayout(self.ide_view)
        ide_layout.setContentsMargins(0, 0, 0, 0)
        ide_layout.setSpacing(0)

        # IDE options with features
        # Icon files should be placed in assets/icons/ (e.g., vscode.png, jetbrains.png)
        self.ide_options = [
            {
                "id": "vscode",
                "name": "VS Code",
                "icon": "vscode",  # assets/icons/vscode.png
                "fallback": "💻",
                "features": ["GitHub Copilot", "Claude", "Gemini", "Codex"],
            },
            {
                "id": "jetbrains",
                "name": "JetBrains",
                "icon": "jetbrains",  # assets/icons/jetbrains.png
                "fallback": "🧠",
                "features": ["GitHub Copilot", "Claude", "Codex"],
            },
            {
                "id": "desktop",
                "name": "Desktop / CLI",
                "icon": "terminal",  # assets/icons/terminal.png
                "fallback": "🖥️",
                "features": ["Claude Code", "Claude Desktop", "Codex CLI", "Gemini CLI"],
            },
        ]

        self.selected_ide = None
        self.ide_cards = {}

        # Horizontal card container
        cards_container = QWidget()
        cards_layout = QHBoxLayout(cards_container)
        cards_layout.setSpacing(12)
        cards_layout.setContentsMargins(0, 0, 0, 0)

        for ide in self.ide_options:
            card = self._create_ide_card(ide)
            self.ide_cards[ide["id"]] = card
            cards_layout.addWidget(card)

        # Center the cards
        center = QWidget()
        center_layout = QHBoxLayout(center)
        center_layout.setContentsMargins(0, 0, 0, 0)
        center_layout.addStretch()
        center_layout.addWidget(cards_container)
        center_layout.addStretch()
        ide_layout.addWidget(center)

        self.stack.addWidget(self.ide_view)

    def _create_ide_card(self, ide: dict) -> QFrame:
        """Create a vertical IDE selection card."""
        card = QFrame()
        card.setObjectName("ideCard")
        card.setFixedSize(150, 175)
        card.setCursor(Qt.CursorShape.PointingHandCursor)
        card.setProperty("ide_id", ide["id"])

        layout = QVBoxLayout(card)
        layout.setContentsMargins(16, 14, 16, 14)
        layout.setSpacing(4)

        # Icon (from file or fallback to emoji)
        icon_label = QLabel()
        icon_label.setObjectName("ideIcon")
        icon_label.setFixedSize(32, 32)
        icon_path = get_icon_path(ide.get("icon", ""))
        if icon_path:
            pixmap = QPixmap(str(icon_path))
            icon_label.setPixmap(pixmap.scaled(32, 32, Qt.AspectRatioMode.KeepAspectRatio, Qt.TransformationMode.SmoothTransformation))
        else:
            icon_label.setText(ide.get("fallback", ""))
            icon_label.setStyleSheet("font-size: 24px; background: transparent;")
        layout.addWidget(icon_label)

        layout.addSpacing(4)

        # Title
        name_label = QLabel(ide["name"])
        name_label.setObjectName("ideName")
        layout.addWidget(name_label)

        layout.addSpacing(4)

        # Features list
        for feature in ide["features"]:
            feat_label = QLabel(f"•  {feature}")
            feat_label.setObjectName("ideFeature")
            layout.addWidget(feat_label)

        layout.addStretch()

        # Apply initial style
        self._apply_ide_card_style(card, selected=False)

        # Make card clickable
        card.mousePressEvent = lambda e, ide_id=ide["id"]: self._select_ide(ide_id)

        return card

    def _apply_ide_card_style(self, card: QFrame, selected: bool):
        """Apply styling to an IDE card."""
        if selected:
            card.setStyleSheet("""
                QFrame#ideCard {
                    background-color: #2a2a2a;
                    border: 2px solid #3498db;
                    border-radius: 10px;
                }
                QLabel#ideIcon {
                    background: transparent;
                }
                QLabel#ideName {
                    font-size: 15px;
                    font-weight: bold;
                    color: white;
                    background: transparent;
                }
                QLabel#ideFeature {
                    font-size: 11px;
                    color: #aaaaaa;
                    background: transparent;
                }
            """)
        else:
            card.setStyleSheet("""
                QFrame#ideCard {
                    background-color: #1e1e1e;
                    border: 1px solid #333333;
                    border-radius: 10px;
                }
                QFrame#ideCard:hover {
                    background-color: #252525;
                    border-color: #444444;
                }
                QLabel#ideIcon {
                    background: transparent;
                }
                QLabel#ideName {
                    font-size: 15px;
                    font-weight: bold;
                    color: white;
                    background: transparent;
                }
                QLabel#ideFeature {
                    font-size: 11px;
                    color: #666666;
                    background: transparent;
                }
            """)

    def _select_ide(self, ide_id: str):
        """Select an IDE and update visual state."""
        self.selected_ide = ide_id

        # Update all card styles
        for card_id, card in self.ide_cards.items():
            self._apply_ide_card_style(card, selected=(card_id == ide_id))

        # Update button state
        if self._button_callback:
            self._button_callback()

    def _setup_provider_selection(self):
        """Setup the provider selection view (step 2)."""
        self.provider_view = QWidget()
        provider_layout = QVBoxLayout(self.provider_view)
        provider_layout.setContentsMargins(0, 0, 0, 0)
        provider_layout.setSpacing(8)

        # Provider cards container (no scroll)
        self.providers_container = QWidget()
        self.providers_container.setFixedWidth(400)
        self.providers_layout = QVBoxLayout(self.providers_container)
        self.providers_layout.setSpacing(0)
        self.providers_layout.setContentsMargins(0, 0, 0, 0)

        # Center the container
        center = QWidget()
        center_layout = QHBoxLayout(center)
        center_layout.setContentsMargins(0, 0, 0, 0)
        center_layout.addStretch()
        center_layout.addWidget(self.providers_container)
        center_layout.addStretch()
        provider_layout.addWidget(center)

        self.stack.addWidget(self.provider_view)

        # Provider definitions - each extension/app has different MCP config locations
        # Note: vscode_claude and claude_code both configure ~/.claude.json (same file)
        # Icon files should be in assets/icons/ (e.g., copilot.png, claude.png)
        self.provider_definitions = {
            "vscode": [
                {"id": "vscode_copilot", "name": "GitHub Copilot", "desc": "mcp.json", "icon": "copilot"},
                {"id": "vscode_claude", "name": "Claude", "desc": "Anthropic", "icon": "claude"},
                {"id": "vscode_gemini", "name": "Gemini", "desc": "Google Code Assist", "icon": "gemini"},
                {"id": "vscode_codex", "name": "Codex", "desc": "OpenAI ChatGPT", "icon": "codex"},
            ],
            "jetbrains": [
                {"id": "jetbrains_copilot", "name": "GitHub Copilot", "desc": "JetBrains plugin", "icon": "copilot"},
                {"id": "jetbrains_claude", "name": "Claude", "desc": "Anthropic plugin", "icon": "claude"},
                {"id": "jetbrains_codex", "name": "Codex", "desc": "OpenAI plugin", "icon": "codex"},
            ],
            "desktop": [
                {"id": "claude_code", "name": "Claude Code", "desc": "CLI tool", "icon": "claude"},
                {"id": "claude_desktop", "name": "Claude Desktop", "desc": "Desktop app", "icon": "claude"},
                {"id": "codex_cli", "name": "Codex CLI", "desc": "OpenAI CLI", "icon": "codex"},
                {"id": "gemini_cli", "name": "Gemini CLI", "desc": "Google CLI", "icon": "gemini"},
            ],
        }

        self.provider_checkboxes = {}

    def _setup_results_view(self):
        """Setup the installation results view (step 2)."""
        self.results_view = QWidget()
        results_layout = QVBoxLayout(self.results_view)
        results_layout.setContentsMargins(0, 10, 0, 0)
        results_layout.setSpacing(10)

        # Container for results with scroll
        scroll = QScrollArea()
        scroll.setWidgetResizable(True)
        scroll.setHorizontalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOff)
        scroll.setStyleSheet("""
            QScrollArea {
                border: none;
                background: transparent;
            }
            QScrollArea > QWidget > QWidget {
                background: transparent;
            }
        """)

        self.results_container = QWidget()
        self.results_layout = QVBoxLayout(self.results_container)
        self.results_layout.setContentsMargins(20, 0, 20, 0)
        self.results_layout.setSpacing(8)
        self.results_layout.addStretch()

        scroll.setWidget(self.results_container)
        results_layout.addWidget(scroll)

        self.stack.addWidget(self.results_view)

    def _populate_results(self):
        """Populate results based on installation outcomes."""
        # Clear existing results
        while self.results_layout.count():
            item = self.results_layout.takeAt(0)
            if item.widget():
                item.widget().deleteLater()

        # Add result cards for each configured provider
        for provider_id, (success, message) in self._install_results.items():
            card = self._create_result_card(provider_id, success, message)
            self.results_layout.addWidget(card)

        self.results_layout.addStretch()

    def _create_result_card(self, provider_id: str, success: bool, message: str) -> QFrame:
        """Create a result card showing success/failure for a provider."""
        # Find provider info
        provider_name = provider_id
        provider_icon = None
        for ide_providers in self.provider_definitions.values():
            for p in ide_providers:
                if p["id"] == provider_id:
                    provider_name = p["name"]
                    provider_icon = p.get("icon")
                    break

        card = QFrame()
        card.setObjectName("resultCard")
        card.setFixedHeight(50)
        card.setStyleSheet(f"""
            QFrame#resultCard {{
                background-color: #1e1e1e;
                border: 1px solid {"#22C55E" if success else "#EF4444"};
                border-radius: 8px;
            }}
            QLabel {{
                background: transparent;
            }}
        """)

        layout = QHBoxLayout(card)
        layout.setContentsMargins(12, 8, 12, 8)
        layout.setSpacing(10)

        # Status icon
        status_icon = QLabel("✓" if success else "✗")
        status_icon.setStyleSheet(f"""
            font-family: 'Segoe UI Symbol', 'Segoe UI';
            font-size: 16px;
            font-weight: bold;
            color: {"#22C55E" if success else "#EF4444"};
        """)
        layout.addWidget(status_icon)

        # Provider icon if available
        if provider_icon:
            icon_path = get_icon_path(provider_icon)
            if icon_path:
                icon_label = QLabel()
                icon_label.setFixedSize(24, 24)
                pixmap = QPixmap(str(icon_path))
                icon_label.setPixmap(pixmap.scaled(24, 24, Qt.AspectRatioMode.KeepAspectRatio, Qt.TransformationMode.SmoothTransformation))
                layout.addWidget(icon_label)

        # Provider name and message
        text_label = QLabel(
            f'<div style="line-height: 1.1;">'
            f'<span style="font-size: 13px; font-weight: bold; color: white;">{provider_name}</span><br>'
            f'<span style="font-size: 10px; color: #888888;">{message}</span>'
            f'</div>'
        )
        layout.addWidget(text_label)
        layout.addStretch()

        return card

    def set_toolkit_path(self, path: str | None):
        """Set the toolkit path for MCP configuration."""
        self._toolkit_path = path

    def _get_selected_providers(self) -> list[str]:
        """Get list of selected provider IDs."""
        return [pid for pid, cb in self.provider_checkboxes.items() if cb.isChecked()]

    def _run_installation(self):
        """Run MCP configuration for selected providers."""
        if not SETUP_AVAILABLE:
            self._install_results = {
                "error": (False, "Setup module not available")
            }
            self._is_installing = False
            self._go_to_step(2)
            return

        if not self._toolkit_path:
            self._install_results = {
                "error": (False, "Toolkit path not set")
            }
            self._is_installing = False
            self._go_to_step(2)
            return

        script_dir = Path(self._toolkit_path) / "hytale-rag"
        if not script_dir.exists():
            self._install_results = {
                "error": (False, f"Script directory not found: {script_dir}")
            }
            self._is_installing = False
            self._go_to_step(2)
            return

        selected = self._get_selected_providers()
        self._install_results = {}

        # First, ensure npm dependencies are installed
        if self._node_installed:
            try:
                result = subprocess.run(
                    ["npm", "install"],
                    cwd=str(script_dir),
                    capture_output=True,
                    text=True,
                    timeout=120,
                    shell=(sys.platform == "win32")
                )
                if result.returncode != 0:
                    # Non-fatal warning - dependencies might already be installed
                    print(f"npm install warning: {result.stderr[:200] if result.stderr else 'unknown error'}")
            except Exception as e:
                print(f"npm install warning: {e}")

        # Mapping from GUI provider IDs to setup functions
        # Some providers share the same config file (e.g., vscode_claude and claude_code both use .claude.json)
        provider_setup_map = {
            # VS Code providers
            "vscode_copilot": ("vscode", setup_vscode, "VS Code settings"),
            "vscode_claude": ("claude_code", setup_claude_code, "~/.claude.json"),
            "vscode_gemini": (None, None, "Not yet supported"),
            "vscode_codex": (None, None, "Not yet supported"),
            # JetBrains providers
            "jetbrains_copilot": ("jetbrains", setup_jetbrains, "JetBrains MCP config"),
            "jetbrains_claude": ("claude_code", setup_claude_code, "~/.claude.json"),
            "jetbrains_codex": (None, None, "Not yet supported"),
            # Desktop/CLI providers
            "claude_code": ("claude_code", setup_claude_code, "~/.claude.json"),
            "claude_desktop": ("claude_desktop", self._setup_claude_desktop, "Claude Desktop config"),
            "codex_cli": ("codex", setup_codex, "~/.codex/config.toml"),
            "gemini_cli": (None, None, "Not yet supported"),
        }

        # Track which setup functions we've already run to avoid duplicates
        # (e.g., if both vscode_claude and claude_code are selected, only run setup_claude_code once)
        executed_setups = set()

        # Create start scripts if needed for certain providers
        needs_scripts = any(
            pid in selected for pid in ["vscode_copilot", "jetbrains_copilot", "codex_cli"]
        )
        if needs_scripts:
            try:
                create_start_scripts(script_dir)
            except Exception as e:
                print(f"Warning: Could not create start scripts: {e}")

        for provider_id in selected:
            if provider_id not in provider_setup_map:
                self._install_results[provider_id] = (False, "Unknown provider")
                continue

            setup_key, setup_fn, config_desc = provider_setup_map[provider_id]

            if setup_fn is None:
                self._install_results[provider_id] = (False, config_desc)
                continue

            # Skip if we've already run this setup function
            if setup_key in executed_setups:
                self._install_results[provider_id] = (True, f"Configured ({config_desc})")
                continue

            try:
                success = setup_fn(script_dir)
                executed_setups.add(setup_key)
                if success:
                    self._install_results[provider_id] = (True, f"Configured {config_desc}")
                else:
                    self._install_results[provider_id] = (False, "Configuration failed")
            except Exception as e:
                self._install_results[provider_id] = (False, str(e)[:50])

        self._is_installing = False
        self._go_to_step(2)

    def _setup_claude_desktop(self, script_dir: Path) -> bool:
        """Configure Claude Desktop MCP server."""
        import platform
        system = platform.system()

        if system == "Windows":
            appdata = os.environ.get("APPDATA") or str(Path.home() / "AppData" / "Roaming")
            config_path = Path(appdata) / "Claude" / "claude_desktop_config.json"
        elif system == "Darwin":
            config_path = Path.home() / "Library" / "Application Support" / "Claude" / "claude_desktop_config.json"
        else:
            config_path = Path.home() / ".config" / "Claude" / "claude_desktop_config.json"

        # Create parent directory if needed
        config_path.parent.mkdir(parents=True, exist_ok=True)

        mcp_config = get_mcp_command_stdio(script_dir)

        if config_path.exists():
            try:
                config = json.loads(config_path.read_text(encoding='utf-8'))
            except json.JSONDecodeError:
                config = {}
        else:
            config = {}

        if "mcpServers" not in config:
            config["mcpServers"] = {}

        config["mcpServers"]["hytale-rag"] = mcp_config
        config_path.write_text(json.dumps(config, indent=2), encoding='utf-8')

        return True

    def _populate_providers(self):
        """Populate providers based on selected IDE."""
        # Clear existing providers
        while self.providers_layout.count():
            item = self.providers_layout.takeAt(0)
            if item.widget():
                item.widget().deleteLater()

        self.provider_checkboxes.clear()

        if not self.selected_ide:
            no_selection = QLabel("No IDE selected. Go back to select one.")
            no_selection.setStyleSheet("color: #888888; font-size: 13px;")
            no_selection.setAlignment(Qt.AlignmentFlag.AlignCenter)
            self.providers_layout.addWidget(no_selection)
            return

        if self.selected_ide not in self.provider_definitions:
            return

        providers = self.provider_definitions[self.selected_ide]
        if not providers:
            return

        # Provider cards (no header needed for single IDE)
        for provider in providers:
            card = self._create_provider_card(provider)
            self.providers_layout.addWidget(card)

        self.providers_layout.addStretch()

    def _create_provider_card(self, provider: dict) -> QFrame:
        """Create a provider selection card."""
        card = QFrame()
        card.setObjectName("providerCard")
        card.setFixedHeight(50)
        card.setCursor(Qt.CursorShape.PointingHandCursor)
        card.setStyleSheet("""
            QFrame#providerCard {
                background-color: #1e1e1e;
                border: 1px solid #3a3a3a;
                border-radius: 8px;
            }
            QFrame#providerCard:hover {
                border-color: #3498db;
            }
            QLabel#providerIcon, QLabel#providerText {
                background: transparent;
            }
            QCheckBox#providerCheck {
                background: transparent;
            }
            QCheckBox#providerCheck::indicator {
                width: 18px;
                height: 18px;
                border-radius: 4px;
                border: 2px solid #555;
                background-color: #2b2b2b;
            }
            QCheckBox#providerCheck::indicator:checked {
                background-color: #22C55E;
                border-color: #22C55E;
            }
            QCheckBox#providerCheck::indicator:hover {
                border-color: #3498db;
            }
        """)

        layout = QHBoxLayout(card)
        layout.setContentsMargins(14, 0, 14, 0)
        layout.setSpacing(10)

        # Checkbox
        checkbox = QCheckBox()
        checkbox.setObjectName("providerCheck")
        checkbox.setChecked(False)
        # Connect checkbox state change to update button text (Install/Skip)
        checkbox.stateChanged.connect(self._on_provider_changed)
        self.provider_checkboxes[provider["id"]] = checkbox
        layout.addWidget(checkbox)

        # Icon (if available)
        icon_name = provider.get("icon", "")
        icon_path = get_icon_path(icon_name) if icon_name else None
        if icon_path:
            icon_label = QLabel()
            icon_label.setObjectName("providerIcon")
            icon_label.setFixedSize(24, 24)
            pixmap = QPixmap(str(icon_path))
            icon_label.setPixmap(pixmap.scaled(24, 24, Qt.AspectRatioMode.KeepAspectRatio, Qt.TransformationMode.SmoothTransformation))
            layout.addWidget(icon_label)

        # Text as single label with HTML
        text_label = QLabel(
            f'<div style="line-height: 1.1;">'
            f'<span style="font-size: 13px; font-weight: bold; color: white;">{provider["name"]}</span><br>'
            f'<span style="font-size: 10px; color: #888888;">{provider["desc"]}</span>'
            f'</div>'
        )
        text_label.setObjectName("providerText")
        layout.addWidget(text_label)
        layout.addStretch()

        # Make card clickable
        card.mousePressEvent = lambda e: checkbox.toggle()

        return card

    def set_button_callback(self, callback):
        self._button_callback = callback

    def set_back_button_callback(self, callback):
        self._back_button_callback = callback

    def _on_provider_changed(self, state):
        """Called when a provider checkbox is toggled."""
        # Update the button text to reflect Install/Skip
        if self._button_callback:
            self._button_callback()

    def get_next_button_config(self) -> dict:
        if self._is_installing:
            return {"text": "Installing...", "style": "disabled", "enabled": False}
        if self._current_step == 0:
            # Check if an IDE is selected
            if self.selected_ide:
                return {"text": "Next", "style": "primary", "enabled": True}
            else:
                return {"text": "Skip", "style": "secondary", "enabled": True}
        elif self._current_step == 1:
            # Provider selection - show Install if providers selected, Skip otherwise
            selected = self._get_selected_providers()
            if selected:
                return {"text": "Install", "style": "action", "enabled": True}
            else:
                return {"text": "Skip", "style": "secondary", "enabled": True}
        else:
            # Step 2 - results view
            return {"text": "Next", "style": "primary", "enabled": True}

    def get_back_button_config(self) -> dict:
        if self._is_installing:
            return {"text": "Back", "style": "default", "enabled": False}
        if self._current_step == 1:
            return {"text": "Back", "style": "default", "enabled": True}
        elif self._current_step == 2:
            # On results page, can't go back (already installed)
            return {"text": "Back", "style": "default", "enabled": False}
        else:
            return {"text": "Back", "style": "default", "enabled": True}

    def should_run_action(self) -> bool:
        """Check if we should handle navigation internally."""
        if self._current_step == 0:
            # Go to step 1 if an IDE is selected
            if self.selected_ide:
                return True
        elif self._current_step == 1:
            # Run installation if providers are selected
            selected = self._get_selected_providers()
            if selected:
                return True
        return False

    def run_internal_action(self):
        """Handle internal navigation and installation."""
        if self._current_step == 0:
            self._go_to_step(1)
        elif self._current_step == 1:
            # Run installation
            self._is_installing = True
            if self._button_callback:
                self._button_callback()
            # Use QTimer to allow UI to update before running installation
            QTimer.singleShot(100, self._run_installation)

    def handle_back(self) -> bool:
        """Handle back button. Returns True if handled internally."""
        if self._current_step == 1:
            self._go_to_step(0)
            return True
        # Can't go back from results (step 2)
        return False

    def _go_to_step(self, step: int):
        """Navigate to a specific step."""
        self._current_step = step
        self.stack.setCurrentIndex(step)

        if step == 0:
            self.title.setText("Select Your IDE")
            self.desc.setText(
                "Choose your development environment.\n"
                "We'll configure the MCP server for it."
            )
            self.node_status.show()
            self.note.show()
        elif step == 1:
            self.title.setText("Select Extensions")
            self.desc.setText(
                "Choose which extensions to configure.\n"
                "Each has its own MCP configuration."
            )
            self._populate_providers()
            self.node_status.show()
            self.note.show()
        else:
            # Step 2 - results
            self.title.setText("Configuration Complete")
            all_success = all(success for success, _ in self._install_results.values())
            if all_success and self._install_results:
                self.desc.setText(
                    "MCP server has been configured for your selected tools.\n"
                    "Restart your IDE to activate the integration."
                )
            elif self._install_results:
                self.desc.setText(
                    "Some configurations completed with issues.\n"
                    "Check the results below for details."
                )
            else:
                self.desc.setText("No providers were configured.")
            self._populate_results()
            self.node_status.hide()
            self.note.hide()

        if self._button_callback:
            self._button_callback()
        if self._back_button_callback:
            self._back_button_callback()

    def _check_node(self):
        """Check if Node.js is installed and update UI accordingly."""
        self._node_installed, self._node_version = check_node_installed()
        if self._node_installed:
            self.node_status.setText(
                f'<span style="color: #22C55E; font-family: Segoe UI Symbol, Segoe UI;">✓</span> '
                f'<span style="color: #888888; font-size: 11px;">Node.js {self._node_version} found</span>'
            )
            self.node_status.setStyleSheet("font-size: 12px;")
        else:
            self.node_status.setText(
                '<span style="color: #F59E0B;">⚠️ Node.js not found</span> · '
                '<span style="color: #888888; font-size: 11px;">Required for MCP server · </span>'
                '<a href="https://nodejs.org/" style="color: #3498db; font-size: 11px;">Download</a>'
            )
            self.node_status.setStyleSheet("font-size: 12px;")
        self.node_status.show()

    def get_settings(self) -> dict:
        """Return the integration settings."""
        return {
            "ide": self.selected_ide,
            "providers": {id: cb.isChecked() for id, cb in self.provider_checkboxes.items()},
            "node_installed": self._node_installed,
        }


class CLIToolsPage(QWidget):
    """Page for installing CLI tools (hytale-mod command)."""

    # Signals for state changes
    state_changed = pyqtSignal(str)  # idle, running, completed, failed, already_installed

    def __init__(self, parent=None):
        super().__init__(parent)
        self._button_callback = None
        self._back_button_callback = None
        self._state = "idle"  # idle, running, completed, failed, already_installed
        self._process = None
        self._toolkit_path = None
        self._installed_version = None
        self._is_reinstall = False

        layout = QVBoxLayout(self)
        layout.setContentsMargins(40, 40, 40, 30)

        # Title
        self.title = QLabel("Install CLI Tools")
        self.title.setStyleSheet("font-size: 22px; font-weight: bold; color: white;")
        self.title.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(self.title)

        # Description
        self.desc = QLabel(
            "Install the hytale-mod command-line tool for creating new mod projects.\n"
            "This is optional but makes it easy to scaffold mods from anywhere."
        )
        self.desc.setStyleSheet("color: #aaaaaa; font-size: 13px;")
        self.desc.setAlignment(Qt.AlignmentFlag.AlignCenter)
        self.desc.setWordWrap(True)
        layout.addWidget(self.desc)

        layout.addSpacing(20)

        # Stacked widget for settings/terminal views
        self.stack = QStackedWidget()
        layout.addWidget(self.stack, 1)

        # ===== Settings View (index 0) =====
        self.settings_view = QWidget()
        settings_layout = QVBoxLayout(self.settings_view)
        settings_layout.setContentsMargins(0, 10, 0, 0)
        settings_layout.setSpacing(15)

        # Main card
        self.main_card = QFrame()
        self.main_card.setFixedWidth(280)
        self.main_card.setStyleSheet("""
            QFrame#cliCard {
                background-color: #2a2a2a;
                border: 2px solid #3498db;
                border-radius: 10px;
            }
        """)
        self.main_card.setObjectName("cliCard")
        card_layout = QVBoxLayout(self.main_card)
        card_layout.setContentsMargins(18, 15, 18, 15)
        card_layout.setSpacing(6)

        # Header row with title and status
        header_row = QWidget()
        header_layout = QHBoxLayout(header_row)
        header_layout.setContentsMargins(0, 0, 0, 0)
        header_layout.setSpacing(8)

        card_title = QLabel("hytale-mod")
        card_title.setStyleSheet(
            "font-size: 15px; font-weight: bold; color: white; "
            "font-family: 'Consolas', 'Monaco', monospace; background: transparent;"
        )
        header_layout.addWidget(card_title)

        # Installed status badge (hidden by default)
        self.installed_badge = QLabel("✓ Installed")
        self.installed_badge.setStyleSheet(
            "font-family: 'Segoe UI Symbol', 'Segoe UI'; font-size: 10px; color: #22C55E; background: rgba(34, 197, 94, 0.15); "
            "padding: 2px 6px; border-radius: 3px; font-weight: bold;"
        )
        self.installed_badge.hide()
        header_layout.addWidget(self.installed_badge)

        header_layout.addStretch()
        card_layout.addWidget(header_row)

        # Usage example
        usage_label = QLabel("$ hytale-mod init my-awesome-mod")
        usage_label.setStyleSheet(
            "font-size: 11px; color: #22C55E; font-family: 'Consolas', monospace; background: transparent;"
        )
        card_layout.addWidget(usage_label)

        card_layout.addSpacing(8)

        # Feature list
        features = [
            "Create new mod projects instantly",
            "Configures Gradle build system",
            "Sets up IDE integration",
            "Works from any directory",
        ]
        for feature in features:
            feat_label = QLabel(f"•  {feature}")
            feat_label.setStyleSheet("font-size: 11px; color: #999999; background: transparent;")
            card_layout.addWidget(feat_label)

        # Center the card
        card_center = QWidget()
        card_center_layout = QHBoxLayout(card_center)
        card_center_layout.setContentsMargins(0, 0, 0, 0)
        card_center_layout.addStretch()
        card_center_layout.addWidget(self.main_card)
        card_center_layout.addStretch()
        settings_layout.addWidget(card_center)

        settings_layout.addSpacing(20)

        # Install/Reinstall buttons (centered)
        buttons_container = QWidget()
        buttons_layout = QHBoxLayout(buttons_container)
        buttons_layout.setContentsMargins(0, 0, 0, 0)
        buttons_layout.addStretch()

        self.install_btn = QPushButton("Install")
        self.install_btn.setFixedSize(120, 40)
        self.install_btn.setStyleSheet("""
            QPushButton {
                background-color: #22C55E;
                color: white;
                border: none;
                border-radius: 6px;
                font-size: 14px;
                font-weight: bold;
            }
            QPushButton:hover {
                background-color: #16A34A;
            }
        """)
        self.install_btn.clicked.connect(self._start_install)
        buttons_layout.addWidget(self.install_btn)

        # Reinstall button (shown when already installed)
        self.reinstall_btn = QPushButton("Reinstall")
        self.reinstall_btn.setFixedSize(120, 40)
        self.reinstall_btn.setStyleSheet("""
            QPushButton {
                background-color: #3498db;
                color: white;
                border: none;
                border-radius: 6px;
                font-size: 14px;
                font-weight: bold;
            }
            QPushButton:hover {
                background-color: #2980b9;
            }
        """)
        self.reinstall_btn.clicked.connect(self._start_reinstall)
        self.reinstall_btn.hide()
        buttons_layout.addWidget(self.reinstall_btn)

        buttons_layout.addStretch()
        settings_layout.addWidget(buttons_container)

        settings_layout.addStretch()
        self.stack.addWidget(self.settings_view)

        # ===== Terminal View (index 1) =====
        self.terminal_view = QWidget()
        terminal_layout = QVBoxLayout(self.terminal_view)
        terminal_layout.setContentsMargins(0, 0, 0, 0)

        # Terminal output
        self.terminal = QPlainTextEdit()
        self.terminal.setReadOnly(True)
        self.terminal.setStyleSheet("""
            QPlainTextEdit {
                background-color: #1a1a1a;
                color: #00ff00;
                font-family: 'Consolas', 'Monaco', monospace;
                font-size: 11px;
                border: 1px solid #333333;
                border-radius: 6px;
                padding: 10px;
            }
        """)
        terminal_layout.addWidget(self.terminal)

        # Progress bar
        self.progress = QProgressBar()
        self.progress.setTextVisible(False)
        self.progress.setFixedHeight(4)
        self.progress.setStyleSheet("""
            QProgressBar {
                background-color: #333333;
                border: none;
                border-radius: 2px;
            }
            QProgressBar::chunk {
                background-color: #3498db;
                border-radius: 2px;
            }
        """)
        self.progress.setRange(0, 0)  # Indeterminate
        terminal_layout.addWidget(self.progress)

        # Status label
        self.status_label = QLabel("Installing...")
        self.status_label.setStyleSheet("color: #888888; font-size: 11px;")
        self.status_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        terminal_layout.addWidget(self.status_label)

        # Error actions (hidden by default)
        self.error_actions = QWidget()
        self.error_actions.hide()
        error_layout = QHBoxLayout(self.error_actions)
        error_layout.setContentsMargins(0, 10, 0, 0)
        error_layout.addStretch()

        self.retry_btn = QPushButton("Retry")
        self.retry_btn.setStyleSheet("""
            QPushButton {
                background-color: #1f6aa5;
                color: white;
                border: none;
                border-radius: 6px;
                padding: 8px 16px;
                font-size: 12px;
            }
            QPushButton:hover {
                background-color: #2980b9;
            }
        """)
        self.retry_btn.clicked.connect(self._start_install)
        error_layout.addWidget(self.retry_btn)

        error_layout.addStretch()
        terminal_layout.addWidget(self.error_actions)

        self.stack.addWidget(self.terminal_view)

    def set_toolkit_path(self, toolkit_path: str):
        """Set the toolkit path for installation."""
        self._toolkit_path = toolkit_path

    def check_installed(self):
        """Check if hytale-mod CLI is already installed."""
        try:
            # Find Python interpreter (sys.executable is the .exe when frozen)
            if getattr(sys, '_MEIPASS', None):
                python_cmd = "python" if sys.platform == "win32" else "python3"
            else:
                python_cmd = sys.executable

            result = subprocess.run(
                [python_cmd, "-m", "pip", "show", "hytale-mod"],
                capture_output=True,
                text=True,
                timeout=10
            )
            if result.returncode == 0:
                # Parse version from output
                for line in result.stdout.splitlines():
                    if line.startswith("Version:"):
                        self._installed_version = line.split(":", 1)[1].strip()
                        break
                self._update_installed_ui(True)
                return True
        except Exception:
            pass

        self._update_installed_ui(False)
        return False

    def _update_installed_ui(self, is_installed: bool):
        """Update UI based on installation status."""
        if is_installed:
            self._state = "already_installed"
            self.installed_badge.show()
            if self._installed_version:
                self.installed_badge.setText(f"✓ v{self._installed_version}")
            self.install_btn.hide()
            self.reinstall_btn.show()
        else:
            self._state = "idle"
            self._installed_version = None
            self.installed_badge.hide()
            self.install_btn.show()
            self.reinstall_btn.hide()

        self.state_changed.emit(self._state)
        if self._button_callback:
            self._button_callback()

    def _start_reinstall(self):
        """Start reinstallation (with --force-reinstall)."""
        self._is_reinstall = True
        self._start_install()

    def _start_install(self):
        """Start CLI tools installation."""
        self._state = "running"
        self.state_changed.emit("running")
        self.stack.setCurrentIndex(1)
        self.terminal.clear()
        self.error_actions.hide()
        self.progress.setRange(0, 0)
        action = "Reinstalling" if self._is_reinstall else "Installing"
        self.status_label.setText(f"{action} hytale-mod CLI...")

        if self._button_callback:
            self._button_callback()

        # Run pip install
        self._run_pip_install()

    def _run_pip_install(self):
        """Run pip install for the CLI tool."""
        cli_path = "hytale-mod-cli"

        if self._is_reinstall:
            self.terminal.appendPlainText("$ pip install -e hytale-mod-cli/ --force-reinstall\n")
            args = ["-m", "pip", "install", "-e", cli_path, "--force-reinstall"]
        else:
            self.terminal.appendPlainText("$ pip install -e hytale-mod-cli/\n")
            args = ["-m", "pip", "install", "-e", cli_path]

        self._process = QProcess(self)
        self._process.readyReadStandardOutput.connect(self._handle_stdout)
        self._process.readyReadStandardError.connect(self._handle_stderr)
        self._process.finished.connect(self._handle_finished)

        # Install from the toolkit root directory, pointing to the CLI subdirectory
        if self._toolkit_path:
            self._process.setWorkingDirectory(str(self._toolkit_path))

        # Find Python interpreter (sys.executable is the .exe when frozen)
        if getattr(sys, '_MEIPASS', None):
            python_exe = "python" if sys.platform == "win32" else "python3"
        else:
            python_exe = sys.executable
        self._process.start(python_exe, args)

    def _handle_stdout(self):
        """Handle stdout from the process."""
        if self._process:
            data = self._process.readAllStandardOutput()
            text = bytes(data).decode("utf-8", errors="replace")
            self.terminal.appendPlainText(text)

    def _handle_stderr(self):
        """Handle stderr from the process."""
        if self._process:
            data = self._process.readAllStandardError()
            text = bytes(data).decode("utf-8", errors="replace")
            self.terminal.appendPlainText(text)

    def _handle_finished(self, exit_code: int, exit_status):
        """Handle process completion."""
        self._process = None
        was_reinstall = self._is_reinstall
        self._is_reinstall = False

        if exit_code == 0:
            self._state = "completed"
            self.progress.setRange(0, 100)
            self.progress.setValue(100)
            action = "reinstalled" if was_reinstall else "installed"
            self.status_label.setText(f"✓ CLI tools {action} successfully!")
            self.status_label.setStyleSheet("font-family: 'Segoe UI Symbol', 'Segoe UI'; color: #22C55E; font-size: 11px; font-weight: bold;")
            self.terminal.appendPlainText(f"\n✓ Installation complete!")
            self.terminal.appendPlainText("You can now run: hytale-mod init <project-name>")
        else:
            self._state = "failed"
            self.progress.setRange(0, 100)
            self.progress.setValue(0)
            self.status_label.setText("✗ Installation failed")
            self.status_label.setStyleSheet("font-family: 'Segoe UI Symbol', 'Segoe UI'; color: #EF4444; font-size: 11px; font-weight: bold;")
            self.terminal.appendPlainText(f"\n✗ Installation failed (exit code: {exit_code})")
            self.error_actions.show()

        self.state_changed.emit(self._state)
        if self._button_callback:
            self._button_callback()

    def get_state(self) -> str:
        """Get current page state."""
        return self._state

    def can_proceed(self) -> bool:
        """Check if user can proceed to next page."""
        return self._state in ["completed", "idle", "failed", "already_installed"]

    def set_button_callback(self, callback):
        """Set callback for button state updates."""
        self._button_callback = callback

    def set_back_button_callback(self, callback):
        """Set callback for back button state updates."""
        self._back_button_callback = callback

    def get_next_button_config(self) -> dict:
        """Get next button configuration based on state."""
        if self._state == "running":
            return {"text": "Installing...", "style": "disabled", "enabled": False}
        elif self._state in ["completed", "failed"]:
            return {"text": "Next", "style": "primary", "enabled": True}
        elif self._state == "already_installed":
            return {"text": "Next", "style": "primary", "enabled": True}
        else:  # idle
            return {"text": "Skip", "style": "primary", "enabled": True}

    def get_back_button_config(self) -> dict:
        """Get back button configuration based on state."""
        if self._state == "running":
            return {"text": "Cancel", "style": "danger", "enabled": True}
        else:
            return {"text": "Back", "style": "default", "enabled": True}

    def cancel_install(self):
        """Cancel the installation process."""
        if self._process and self._process.state() == QProcess.ProcessState.Running:
            self._process.kill()
            self._process.waitForFinished(1000)
        self._process = None
        self._is_reinstall = False
        self.stack.setCurrentIndex(0)
        # Re-check installation status to restore proper state
        self.check_installed()
        if self._back_button_callback:
            self._back_button_callback()


class CompletePage(QWidget):
    """Final page with setup summary and next steps."""

    def __init__(self, parent=None):
        super().__init__(parent)

        layout = QVBoxLayout(self)
        layout.setContentsMargins(40, 40, 40, 30)

        # Success header
        success_label = QLabel("Setup Complete!")
        success_label.setStyleSheet(
            "font-size: 24px; font-weight: bold; color: #22C55E;"
        )
        success_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(success_label)

        # Subtitle
        subtitle = QLabel("The Hytale Toolkit is ready to use")
        subtitle.setStyleSheet("font-size: 13px; color: #888888;")
        subtitle.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(subtitle)

        layout.addSpacing(20)

        # Next steps cards (2x2 grid)
        steps_container = QWidget()
        steps_layout = QGridLayout(steps_container)
        steps_layout.setSpacing(16)
        steps_layout.setContentsMargins(0, 0, 0, 0)

        step1 = self._create_step_card(
            "🚀",
            "Open your editor",
            "Launch your IDE with MCP configured",
        )
        step2 = self._create_step_card(
            "💬",
            "Ask questions",
            "Query Hytale APIs and game mechanics",
        )
        step3 = self._create_step_card(
            "📚",
            "Browse source",
            "Explore generated source and docs",
        )
        step4 = self._create_step_card(
            "🛠️",
            "Create a mod",
            "Run hytale-mod init to create a new project",
        )

        # 2x2 grid layout
        steps_layout.addWidget(step1, 0, 0)
        steps_layout.addWidget(step2, 0, 1)
        steps_layout.addWidget(step3, 1, 0)
        steps_layout.addWidget(step4, 1, 1)

        # Center steps
        steps_center = QWidget()
        steps_center_layout = QHBoxLayout(steps_center)
        steps_center_layout.setContentsMargins(0, 0, 0, 0)
        steps_center_layout.addStretch()
        steps_center_layout.addWidget(steps_container)
        steps_center_layout.addStretch()
        layout.addWidget(steps_center)

        layout.addSpacing(15)

        # Footer note
        footer = QLabel("Restart your editor for changes to take effect")
        footer.setStyleSheet("font-size: 11px; color: #666666;")
        footer.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(footer)

        layout.addStretch()

    def _create_step_card(self, icon: str, title: str, description: str) -> QFrame:
        """Create a next step card (matches Welcome page style)."""
        card = QFrame()
        card.setObjectName("stepCard")
        card.setFixedSize(170, 120)
        card.setStyleSheet("""
            QFrame#stepCard {
                background-color: #2b2b2b;
                border: 1px solid #3a3a3a;
                border-radius: 8px;
            }
            QFrame#stepCard:hover {
                border-color: #3498db;
            }
            QLabel {
                background: transparent;
                border: none;
            }
        """)

        layout = QVBoxLayout(card)
        layout.setContentsMargins(15, 15, 15, 15)
        layout.setSpacing(5)
        layout.setAlignment(Qt.AlignmentFlag.AlignCenter)

        # Icon
        icon_label = QLabel(icon)
        icon_label.setStyleSheet(
            "font-family: 'Segoe UI Emoji'; font-size: 26px; color: #3498db;"
        )
        icon_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(icon_label)

        # Title
        title_label = QLabel(title)
        title_label.setStyleSheet(
            "font-family: 'Segoe UI'; font-size: 14px; font-weight: bold; color: white;"
        )
        title_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(title_label)

        # Description
        desc_label = QLabel(description)
        desc_label.setStyleSheet(
            "font-family: 'Segoe UI'; font-size: 11px; color: #999999;"
        )
        desc_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        desc_label.setWordWrap(True)
        layout.addWidget(desc_label)

        return card

    def set_summary(self, settings: dict):
        """Accept settings (kept for compatibility)."""
        pass


class SetupWizard(QMainWindow):
    """Main wizard window."""

    def __init__(self):
        super().__init__()
        self.setWindowTitle("Hytale Toolkit Setup")
        self.setFixedSize(750, 550)

        # Dark theme
        self.setStyleSheet("""
            QMainWindow, QWidget {
                background-color: #2b2b2b;
                color: white;
            }
            QPushButton {
                background-color: #1f6aa5;
                color: white;
                border: none;
                border-radius: 6px;
                padding: 10px 25px;
                font-size: 13px;
            }
            QPushButton:hover {
                background-color: #2980b9;
            }
            QPushButton:pressed {
                background-color: #1a5276;
            }
            QPushButton#backButton {
                background-color: transparent;
                border: 1px solid #555;
                padding: 0px 25px;
                min-height: 38px;
            }
            QPushButton#backButton:hover {
                background-color: #3a3a3a;
            }
        """)

        # Central widget
        central = QWidget()
        self.setCentralWidget(central)
        main_layout = QHBoxLayout(central)
        main_layout.setContentsMargins(0, 0, 0, 0)
        main_layout.setSpacing(0)

        # Sidebar
        self.sidebar = SidebarWidget()
        main_layout.addWidget(self.sidebar)

        # Content area
        content_widget = QWidget()
        content_layout = QVBoxLayout(content_widget)
        content_layout.setContentsMargins(0, 0, 0, 0)

        # Page stack
        self.page_stack = QStackedWidget()
        self.pages = [
            WelcomePage(),
            HytalePathPage(),
            DecompilePage(),
            JavadocsPage(),
            ProviderPage(),
            DatabasePage(),
            IntegrationPage(),
            CLIToolsPage(),
            CompletePage(),
        ]
        for page in self.pages:
            self.page_stack.addWidget(page)

        content_layout.addWidget(self.page_stack)

        # Button bar
        button_bar = QWidget()
        button_bar.setFixedHeight(60)
        button_layout = QHBoxLayout(button_bar)
        button_layout.setContentsMargins(20, 10, 20, 15)

        # Version label on the left
        version_label = QLabel(f"v{__version__}")
        version_label.setStyleSheet(
            "color: #666666; font-size: 11px; padding-top: 15px;"
        )
        button_layout.addWidget(version_label)

        button_layout.addStretch()

        self.back_btn = QPushButton("Back")
        self.back_btn.setObjectName("backButton")
        self.back_btn.clicked.connect(self.go_back)
        self.back_btn.hide()  # Hidden on first page
        button_layout.addWidget(self.back_btn)

        self.next_btn = QPushButton("Next")
        self.next_btn.clicked.connect(self.go_next)
        button_layout.addWidget(self.next_btn)

        content_layout.addWidget(button_bar)
        main_layout.addWidget(content_widget)

        self.current_page = 0
        self.show_page(0)

    def show_page(self, index: int):
        """Show a specific page."""
        # Save config when leaving certain pages
        prev_page = self.pages[self.current_page] if hasattr(self, 'current_page') else None
        if prev_page:
            # Save when leaving Paths page (index 1) or Provider page (index 4)
            if isinstance(prev_page, (HytalePathPage, ProviderPage)):
                self.save_config()

        self.current_page = index
        self.page_stack.setCurrentIndex(index)
        self.sidebar.set_step(index)

        # Update button visibility
        self.back_btn.setVisible(index > 0)

        # Pass paths to pages that need them
        page = self.pages[index]
        paths_page = self.pages[1]  # HytalePathPage
        decompile_page = self.pages[2]  # DecompilePage

        if isinstance(page, DecompilePage):
            if hasattr(paths_page, 'get_paths'):
                paths = paths_page.get_paths()
                page.set_paths(paths.get('hytale_path'), paths.get('toolkit_path'))
        elif isinstance(page, JavadocsPage):
            if hasattr(paths_page, 'get_paths') and hasattr(decompile_page, 'get_settings'):
                paths = paths_page.get_paths()
                decompile_settings = decompile_page.get_settings()
                ram_gb = decompile_settings.get('ram_gb', 8) or 8
                # Get local Java path if set
                local_java = getattr(decompile_page, '_local_java_path', None)
                page.set_paths(paths.get('toolkit_path'), ram_gb, local_java)
        elif isinstance(page, ProviderPage):
            if hasattr(paths_page, 'get_paths'):
                paths = paths_page.get_paths()
                page.set_toolkit_path(paths.get('toolkit_path'))
        elif isinstance(page, DatabasePage):
            provider_page = self.pages[4]  # ProviderPage
            if hasattr(paths_page, 'get_paths') and hasattr(provider_page, 'get_settings'):
                paths = paths_page.get_paths()
                provider_settings = provider_page.get_settings()
                page.set_paths(paths.get('toolkit_path'), provider_settings.get('provider'))
        elif isinstance(page, CLIToolsPage):
            if hasattr(paths_page, 'get_paths'):
                paths = paths_page.get_paths()
                page.set_toolkit_path(paths.get('toolkit_path'))
            # Check if CLI tools are already installed
            page.check_installed()
        elif isinstance(page, IntegrationPage):
            if hasattr(paths_page, 'get_paths'):
                paths = paths_page.get_paths()
                page.set_toolkit_path(paths.get('toolkit_path'))

        # Check if page has custom button config
        if hasattr(page, 'get_next_button_config'):
            # Set up callback for dynamic updates
            if hasattr(page, 'set_button_callback'):
                page.set_button_callback(self.update_next_button)
            if hasattr(page, 'set_back_button_callback'):
                page.set_back_button_callback(self.update_back_button)
            self.update_next_button()
            self.update_back_button()
        elif index == len(self.pages) - 1:
            # Last page
            self.next_btn.setText("Finish")
            self.set_next_button_style("primary")
            self.set_back_button_style("default")
        else:
            # Default
            self.next_btn.setText("Next")
            self.set_next_button_style("primary")
            self.set_back_button_style("default")

    def update_next_button(self):
        """Update the next button based on current page's config."""
        page = self.pages[self.current_page]
        if hasattr(page, 'get_next_button_config'):
            config = page.get_next_button_config()
            self.next_btn.setText(config.get("text", "Next"))
            self.set_next_button_style(config.get("style", "primary"))
            self.next_btn.setEnabled(config.get("enabled", True))

    def update_back_button(self):
        """Update the back button based on current page's config."""
        page = self.pages[self.current_page]
        if hasattr(page, 'get_back_button_config'):
            config = page.get_back_button_config()
            self.back_btn.setText(config.get("text", "Back"))
            self.set_back_button_style(config.get("style", "default"))
            self.back_btn.setEnabled(config.get("enabled", True))

    def set_next_button_style(self, style: str):
        """Set the next button's visual style."""
        if style == "action":
            # Green action button
            self.next_btn.setStyleSheet("""
                QPushButton {
                    background-color: #22C55E;
                    color: white;
                    border: none;
                    border-radius: 6px;
                    padding: 0px 25px;
                    min-height: 38px;
                    font-size: 13px;
                    font-weight: bold;
                }
                QPushButton:hover {
                    background-color: #16A34A;
                }
                QPushButton:pressed {
                    background-color: #15803D;
                }
            """)
        elif style == "secondary":
            # Outline/skip button
            self.next_btn.setStyleSheet("""
                QPushButton {
                    background-color: transparent;
                    color: #aaaaaa;
                    border: 1px solid #555555;
                    border-radius: 6px;
                    padding: 0px 25px;
                    min-height: 38px;
                    font-size: 13px;
                }
                QPushButton:hover {
                    background-color: #3a3a3a;
                    color: white;
                }
                QPushButton:pressed {
                    background-color: #333333;
                }
            """)
        elif style == "disabled":
            # Disabled/running button
            self.next_btn.setStyleSheet("""
                QPushButton {
                    background-color: #3a3a3a;
                    color: #666666;
                    border: none;
                    border-radius: 6px;
                    padding: 0px 25px;
                    min-height: 38px;
                    font-size: 13px;
                }
            """)
        else:
            # Primary (default blue)
            self.next_btn.setStyleSheet("""
                QPushButton {
                    background-color: #1f6aa5;
                    color: white;
                    border: none;
                    border-radius: 6px;
                    padding: 0px 25px;
                    min-height: 38px;
                    font-size: 13px;
                }
                QPushButton:hover {
                    background-color: #2980b9;
                }
                QPushButton:pressed {
                    background-color: #1a5276;
                }
            """)

    def set_back_button_style(self, style: str):
        """Set the back button's visual style."""
        if style == "danger":
            # Red cancel button
            self.back_btn.setStyleSheet("""
                QPushButton {
                    background-color: transparent;
                    color: #EF4444;
                    border: 1px solid #EF4444;
                    border-radius: 6px;
                    padding: 0px 25px;
                    min-height: 38px;
                    font-size: 13px;
                }
                QPushButton:hover {
                    background-color: #EF444420;
                }
                QPushButton:pressed {
                    background-color: #EF444440;
                }
            """)
        else:
            # Default back button
            self.back_btn.setStyleSheet("""
                QPushButton {
                    background-color: transparent;
                    color: white;
                    border: 1px solid #555;
                    border-radius: 6px;
                    padding: 0px 25px;
                    min-height: 38px;
                    font-size: 13px;
                }
                QPushButton:hover {
                    background-color: #3a3a3a;
                }
            """)

    def go_next(self):
        """Go to next page, run action, or finish."""
        page = self.pages[self.current_page]

        # Check if page wants to run an action instead of navigating
        if hasattr(page, 'should_run_action') and page.should_run_action():
            if isinstance(page, HytalePathPage):
                page.start_download()
            elif isinstance(page, DecompilePage):
                page.start_decompile()
            elif isinstance(page, JavadocsPage):
                page.start_generate()
            elif isinstance(page, DatabasePage):
                page.start_download()
            elif isinstance(page, IntegrationPage):
                page.run_internal_action()
            return

        # Normal navigation
        if self.current_page < len(self.pages) - 1:
            self.show_page(self.current_page + 1)
        else:
            # Save config before closing
            self.save_config()
            self.close()

    def go_back(self):
        """Go to previous page or cancel current operation."""
        page = self.pages[self.current_page]

        # Check if page is running and should be cancelled
        state = page.get_state() if hasattr(page, 'get_state') else None
        if state in ["running", "downloading"]:
            if isinstance(page, HytalePathPage):
                page.cancel_download()
            elif isinstance(page, DecompilePage):
                page.cancel_decompile()
            elif isinstance(page, JavadocsPage):
                page.cancel_generate()
            elif isinstance(page, DatabasePage):
                page.cancel_download()
            elif isinstance(page, CLIToolsPage):
                page.cancel_install()
            return

        # Check if page handles back internally (e.g., IntegrationPage step navigation)
        if hasattr(page, 'handle_back') and page.handle_back():
            return

        # Normal navigation
        if self.current_page > 0:
            self.show_page(self.current_page - 1)

    def save_config(self):
        """Save current configuration to .env file in the toolkit directory.

        Saves only the essential variables that match setup.py:
        - HYTALE_INSTALL_PATH
        - HYTALE_CLIENT_DATA_DIR (only if exists)
        - HYTALE_DECOMPILED_DIR
        - EMBEDDING_PROVIDER
        - VOYAGE_API_KEY (only if using Voyage)
        - OLLAMA_MODEL (only if using Ollama)
        """
        paths_page = self.pages[1]  # HytalePathPage
        provider_page = self.pages[4]  # ProviderPage

        # Get toolkit path
        if not hasattr(paths_page, 'get_paths'):
            return
        paths = paths_page.get_paths()
        toolkit_path = paths.get('toolkit_path')
        hytale_path = paths.get('hytale_path')

        if not toolkit_path:
            return

        toolkit_path = Path(toolkit_path)
        if not toolkit_path.exists():
            return

        # .env goes in hytale-rag/ subdirectory where the scripts expect it
        hytale_rag_dir = toolkit_path / "hytale-rag"
        if not hytale_rag_dir.exists():
            return

        env_path = hytale_rag_dir / ".env"

        # Build config dictionary
        config = {}

        # Load existing .env if it exists (to preserve any existing values like R2 keys)
        if env_path.exists():
            try:
                with open(env_path, "r", encoding="utf-8") as f:
                    for line in f:
                        line = line.strip()
                        if line and not line.startswith("#") and "=" in line:
                            key, value = line.split("=", 1)
                            config[key.strip()] = value.strip()
            except Exception:
                pass

        # Update with current wizard settings
        if hytale_path:
            config["HYTALE_INSTALL_PATH"] = hytale_path

            # Only save HYTALE_CLIENT_DATA_DIR if it exists
            client_data_dir = Path(hytale_path) / "Client" / "Data"
            if client_data_dir.exists():
                config["HYTALE_CLIENT_DATA_DIR"] = str(client_data_dir)

        config["HYTALE_DECOMPILED_DIR"] = str(toolkit_path / "decompiled")

        # Provider settings
        if hasattr(provider_page, 'get_settings'):
            provider_settings = provider_page.get_settings()
            provider = provider_settings.get('provider', 'voyage')
            api_key = provider_settings.get('api_key', '')

            config["EMBEDDING_PROVIDER"] = provider

            if provider == "voyage":
                if api_key:
                    config["VOYAGE_API_KEY"] = api_key
                # Remove Ollama config if switching to Voyage
                config.pop("OLLAMA_MODEL", None)
            elif provider == "ollama":
                config["OLLAMA_MODEL"] = "nomic-embed-text"
                # Remove Voyage config if switching to Ollama
                config.pop("VOYAGE_API_KEY", None)

        # Write the .env file
        try:
            with open(env_path, "w", encoding="utf-8") as f:
                f.write("# Hytale Toolkit Configuration\n")
                f.write("# Generated by the Setup Wizard\n\n")

                # Write in a logical order (only the essential keys)
                ordered_keys = [
                    "HYTALE_INSTALL_PATH",
                    "HYTALE_CLIENT_DATA_DIR",
                    "HYTALE_DECOMPILED_DIR",
                    "EMBEDDING_PROVIDER",
                    "VOYAGE_API_KEY",
                    "OLLAMA_MODEL",
                ]

                # Write ordered keys first
                for key in ordered_keys:
                    if key in config and config[key]:
                        f.write(f"{key}={config[key]}\n")

                # Write any remaining keys (preserved from existing .env, like R2 keys)
                for key, value in config.items():
                    if key not in ordered_keys and value:
                        f.write(f"{key}={value}\n")

        except Exception as e:
            print(f"Warning: Could not save config to {env_path}: {e}")


def main():
    # Enable high DPI scaling for crisp text
    QApplication.setHighDpiScaleFactorRoundingPolicy(
        Qt.HighDpiScaleFactorRoundingPolicy.PassThrough
    )

    app = QApplication(sys.argv)

    # Set application icon (check PyInstaller bundle first)
    if getattr(sys, '_MEIPASS', None):
        icon_path = Path(sys._MEIPASS) / ".github" / "logo-transparent.png"
    else:
        icon_path = Path(__file__).parent.parent / ".github" / "logo-transparent.png"
    if icon_path.exists():
        app.setWindowIcon(QIcon(str(icon_path)))

    # Set application-wide font (with cross-platform fallbacks)
    if sys.platform == "darwin":
        font = QFont("SF Pro", 10)  # macOS system font
    elif sys.platform == "win32":
        font = QFont("Segoe UI", 10)  # Windows system font
    else:
        font = QFont("Ubuntu", 10)  # Linux fallback
    font.setHintingPreference(QFont.HintingPreference.PreferFullHinting)
    app.setFont(font)

    wizard = SetupWizard()
    wizard.show()

    # Center on screen
    screen = app.primaryScreen()
    if screen:
        geo = screen.geometry()
        x = (geo.width() - wizard.width()) // 2
        y = (geo.height() - wizard.height()) // 2
        wizard.move(x, y)

    # Check for updates (non-blocking, after UI is shown)
    QTimer.singleShot(500, lambda: _check_and_prompt_update(wizard))

    sys.exit(app.exec())


def _check_and_prompt_update(parent):
    """Check for updates and show dialog if available."""
    update_info = check_for_updates(__version__)
    if update_info:
        dialog = UpdateDialog(__version__, update_info, parent)
        dialog.exec()


if __name__ == "__main__":
    main()
