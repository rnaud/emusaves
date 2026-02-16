# 📸 Screenshots Directory

This directory contains screenshots and mockups for the EmuSaves app documentation.

## 📱 Needed Screenshots

### Priority Screenshots
- [ ] **Home Screen** - Main dashboard with sync status and folder list
- [ ] **Add Folder** - Folder selection interface with document picker
- [ ] **Synology Config** - NAS configuration dialog
- [ ] **Sync Progress** - Active sync with progress indicators
- [ ] **Settings** - Scheduled sync configuration

### Additional Screenshots  
- [ ] **Empty State** - First-time user experience
- [ ] **Sync Complete** - Success confirmation screen
- [ ] **Error State** - Connection or sync error handling
- [ ] **Folder List** - Multiple emulator folders configured

## 📏 Screenshot Guidelines

### Technical Requirements
- **Resolution**: 1080x1920 (standard Android portrait) 
- **Format**: PNG with 32-bit depth
- **DPI**: 320-480 DPI for crisp display
- **Naming**: `screenshot-[screen-name]-[variant].png`

### Content Guidelines
- **Remove Personal Data**: Blur or replace NAS hostnames, usernames
- **Use Realistic Data**: Show actual emulator folder names (RetroArch, PPSSPP, etc.)
- **Show App States**: Include loading, success, and error states
- **Consistent Theming**: Use the same device theme (light/dark) across screenshots

### Device Recommendations
- **Modern Android**: Android 10+ for current Material Design
- **Clean Interface**: Hide status bar notifications, use consistent time
- **Standard Font**: Keep system font at default size

## 🖼️ Current Status

Currently using placeholder mockups generated with placeholder.com.

**Mockup URLs Used**:
- Home: `https://via.placeholder.com/300x600/1976D2/FFFFFF?text=...`
- Folders: `https://via.placeholder.com/300x600/388E3C/FFFFFF?text=...`  
- Sync: `https://via.placeholder.com/300x600/F57C00/FFFFFF?text=...`
- Config: `https://via.placeholder.com/300x600/7B1FA2/FFFFFF?text=...`
- Schedule: `https://via.placeholder.com/300x600/D32F2F/FFFFFF?text=...`

## 📝 How to Contribute Screenshots

1. **Install App**: Download from [Releases](../../releases)
2. **Take Screenshots**: Use ADB or device screenshot tools
3. **Edit if Needed**: Remove sensitive data, crop consistently
4. **Submit**: 
   - Add files to this directory
   - Update the main README.md to reference real screenshots
   - Open a pull request

### ADB Screenshot Command
```bash
# Connect device and take screenshot
adb shell screencap -p /sdcard/screenshot.png
adb pull /sdcard/screenshot.png ./docs/screenshots/
```

### Mockup Replacement
Once real screenshots are available, replace the placeholder URLs in `/README.md` with relative paths:
```markdown
<img src="docs/screenshots/screenshot-home.png" alt="Home Screen" width="250"/>
```