#!/bin/bash
# EmuSaves Screenshot Helper Script
# Helps capture and organize app screenshots

set -e

SCREENSHOTS_DIR="docs/screenshots"
DEVICE_SCREENSHOT_PATH="/sdcard/emusaves-screenshot.png"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if adb is available
if ! command -v adb &> /dev/null; then
    echo -e "${RED}Error: ADB not found. Please install Android SDK Platform Tools.${NC}"
    exit 1
fi

# Check if device is connected
if ! adb devices | grep -q "device$"; then
    echo -e "${RED}Error: No Android device connected. Please connect a device and enable USB debugging.${NC}"
    exit 1
fi

# Create screenshots directory if it doesn't exist
mkdir -p "$SCREENSHOTS_DIR"

# Function to take and save screenshot
take_screenshot() {
    local name="$1"
    local description="$2"
    local output_file="$SCREENSHOTS_DIR/screenshot-$name.png"
    
    echo -e "${BLUE}📸 Taking screenshot: $description${NC}"
    echo -e "${YELLOW}   👆 Please navigate to the correct screen on your device, then press Enter...${NC}"
    read -r
    
    # Take screenshot on device
    adb shell screencap -p "$DEVICE_SCREENSHOT_PATH"
    
    # Pull screenshot to computer
    adb pull "$DEVICE_SCREENSHOT_PATH" "$output_file"
    
    # Clean up device
    adb shell rm "$DEVICE_SCREENSHOT_PATH"
    
    echo -e "${GREEN}   ✅ Screenshot saved: $output_file${NC}"
    echo
}

echo -e "${GREEN}🎮 EmuSaves Screenshot Helper${NC}"
echo -e "${BLUE}This script will help you take screenshots of the EmuSaves app.${NC}"
echo
echo -e "${YELLOW}Prerequisites:${NC}"
echo "1. Install EmuSaves APK on your Android device"
echo "2. Connect device with USB debugging enabled"  
echo "3. Open EmuSaves app"
echo
echo -e "${YELLOW}Note: Remove any personal information (NAS hostnames, usernames) before committing!${NC}"
echo
read -p "Ready to continue? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Cancelled."
    exit 0
fi

echo -e "${BLUE}📱 Starting screenshot capture session...${NC}"
echo

# Take screenshots for each major screen
take_screenshot "home" "Home Screen (main dashboard with sync status)"
take_screenshot "add-folder" "Add Folder (document picker interface)" 
take_screenshot "folder-list" "Folder List (multiple emulator folders configured)"
take_screenshot "synology-config" "Synology Configuration (NAS setup dialog)"
take_screenshot "sync-progress" "Sync Progress (active sync with progress bar)"
take_screenshot "settings" "Settings (scheduled sync options)"
take_screenshot "sync-complete" "Sync Complete (success confirmation)"

echo -e "${GREEN}🎉 Screenshot capture complete!${NC}"
echo
echo -e "${BLUE}Next steps:${NC}"
echo "1. Review screenshots in $SCREENSHOTS_DIR/"
echo "2. Edit screenshots to remove personal information"
echo "3. Update README.md to use real screenshots instead of placeholders"
echo "4. Commit and push changes"
echo
echo -e "${YELLOW}To replace placeholders in README.md:${NC}"
echo "   Replace placeholder.com URLs with: docs/screenshots/screenshot-[name].png"
echo
echo -e "${GREEN}Thanks for contributing! 📸${NC}"