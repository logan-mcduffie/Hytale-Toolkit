"""
Shared logging utility for Hytale Toolkit scripts.

Provides file-based logging for troubleshooting while keeping console output clean.
"""

import logging
import platform
import sys
from datetime import datetime
from pathlib import Path


def setup_logging(script_name: str, repo_root: Path) -> tuple[logging.Logger, Path]:
    """
    Configure logging for a script.

    Args:
        script_name: Name of the script (used in log filename)
        repo_root: Root directory of the repository (logs go in repo_root/.logs/)

    Returns:
        Tuple of (logger, log_file_path)
    """
    logs_dir = repo_root / ".logs"
    logs_dir.mkdir(exist_ok=True)

    timestamp = datetime.now().strftime("%Y-%m-%d-%H-%M-%S")
    log_file = logs_dir / f"{script_name}-{timestamp}.log"

    logger = logging.getLogger(script_name)
    logger.setLevel(logging.DEBUG)

    # Clear any existing handlers (in case of re-initialization)
    logger.handlers.clear()

    # File handler - detailed DEBUG output
    file_handler = logging.FileHandler(log_file, encoding='utf-8')
    file_handler.setLevel(logging.DEBUG)
    file_handler.setFormatter(logging.Formatter(
        '%(asctime)s [%(levelname)s] %(message)s',
        datefmt='%Y-%m-%d %H:%M:%S'
    ))
    logger.addHandler(file_handler)

    # Log system info at startup
    logger.info(f"{'=' * 60}")
    logger.info(f"=== {script_name} started ===")
    logger.info(f"{'=' * 60}")
    logger.info(f"Timestamp: {datetime.now().isoformat()}")
    logger.info(f"Python: {sys.version}")
    logger.info(f"Platform: {platform.system()} {platform.release()}")
    logger.info(f"Machine: {platform.machine()}")
    logger.info(f"Working directory: {Path.cwd()}")
    logger.info(f"Script location: {repo_root}")
    logger.info(f"Log file: {log_file}")
    logger.info("")

    return logger, log_file


def log_command(logger: logging.Logger, cmd: list[str] | str, exit_code: int, output: str, cwd: Path = None):
    """Log a command execution with its output."""
    cmd_str = cmd if isinstance(cmd, str) else " ".join(cmd)
    logger.debug(f"Command: {cmd_str}")
    if cwd:
        logger.debug(f"  Working dir: {cwd}")
    logger.debug(f"  Exit code: {exit_code}")
    if output:
        for line in output.strip().split('\n')[:50]:  # Limit to first 50 lines
            logger.debug(f"  | {line}")
        if output.count('\n') > 50:
            logger.debug(f"  | ... ({output.count(chr(10)) - 50} more lines)")


def log_exception(logger: logging.Logger, context: str = ""):
    """Log the current exception with full traceback."""
    import traceback
    if context:
        logger.error(f"Exception in {context}:")
    logger.error(traceback.format_exc())


def log_section(logger: logging.Logger, title: str):
    """Log a section header for visual separation."""
    logger.info("")
    logger.info(f"--- {title} ---")
