#!/usr/bin/env python3
"""Generate placeholder PNG screenshots using PIL"""

try:
    from PIL import Image, ImageDraw, ImageFont
except ImportError:
    print("PIL not available. Install with: pip install pillow")
    exit(1)

def create_phone_mockup(title, content_lines, filename):
    """Create a phone mockup PNG"""
    # Create a 300x600 image (phone dimensions)
    img = Image.new('RGB', (300, 600), color='#FAFAFA')
    draw = ImageDraw.Draw(img)
    
    try:
        # Try to use system fonts
        font_large = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 18)
        font_medium = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 14)
        font_small = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 12)
    except:
        try:
            # Fallback fonts
            font_large = ImageFont.truetype('/System/Library/Fonts/Arial.ttf', 18)
            font_medium = ImageFont.truetype('/System/Library/Fonts/Arial.ttf', 14)
            font_small = ImageFont.truetype('/System/Library/Fonts/Arial.ttf', 12)
        except:
            # Use default font as last resort
            font_large = ImageFont.load_default()
            font_medium = ImageFont.load_default()
            font_small = ImageFont.load_default()
    
    # Phone border (rounded rectangle simulation)
    draw.rectangle([0, 0, 300, 600], fill='#000000')
    draw.rectangle([4, 4, 296, 596], fill='#FAFAFA')
    
    # Status bar
    draw.rectangle([4, 4, 296, 28], fill='#1976D2')
    draw.text((8, 8), '9:41', fill='white', font=font_small)
    draw.text((260, 8), '100%', fill='white', font=font_small)
    
    # App bar
    draw.rectangle([4, 28, 296, 84], fill='#E3F2FD')
    draw.text((20, 48), 'EmuSaves', fill='#1976D2', font=font_large)
    
    # Title
    draw.text((20, 100), title, fill='#212121', font=font_medium)
    
    # Content
    y_pos = 140
    for line in content_lines:
        if line.strip():  # Skip empty lines
            # Simple color coding based on content
            if line.startswith('✓'):
                color = '#4CAF50'
            elif line.startswith('📊') or line.startswith('🔄'):
                color = '#1976D2'
            elif '█' in line:  # Progress bar
                color = '#4CAF50'
            else:
                color = '#757575'
            
            draw.text((20, y_pos), line, fill=color, font=font_small)
        y_pos += 22
    
    # Save
    img.save(filename, 'PNG')
    print(f"✓ Created: {filename}")

def main():
    print("📱 Creating EmuSaves Screenshot Placeholders")
    print("=" * 45)
    
    # Home Screen
    create_phone_mockup(
        "📱 Home Screen",
        [
            "📊 Sync Status",
            "Last sync: Feb 16, 01:15",
            "[         Sync Now         ]",
            "",
            "📁 Backup Folders (2)",
            "🎮 RetroArch Saves         ×",
            "📱 PPSSPP Saves           ×", 
            "[⭐ Quick Add] [+ Browse]",
            "",
            "🔧 Synology NAS          ✓",
            "nas.local → /Drive/EmulatorBackups",
            "[   Update Configuration   ]",
            "",
            "⏰ Scheduled Sync        ●",
            "Every 6 hours on Wi-Fi + charging"
        ],
        'screenshot-home.png'
    )
    
    # Quick Add Dialog
    create_phone_mockup(
        "📲 Quick Add Emulators",
        [
            "Select common emulator save locations:",
            "",
            "[🎮 Multi] [🕹️ Console] [📱 Handheld]",
            "",
            "┌─────────────────────────────┐",
            "│🎮 RetroArch Saves           │",
            "│  RetroArch                  │",
            "│  Battery saves and SRAM     │",
            "└─────────────────────────────┘",
            "",
            "┌─────────────────────────────┐",
            "│💾 RetroArch States          │", 
            "│  RetroArch                  │",
            "│  Save states for all cores  │",
            "└─────────────────────────────┘",
            "",
            "┌─────────────────────────────┐",
            "│🎮 Lemuroid Saves    ← [✓]  │",
            "│  Lemuroid                   │", 
            "│  Multi-system saves         │",
            "└─────────────────────────────┘",
            "",
            "                      [Close]"
        ],
        'screenshot-quick-add.png'
    )
    
    # Sync Progress
    create_phone_mockup(
        "🔄 Sync in Progress",
        [
            "🔄 Syncing to nas.local...",
            "████████████░░░░ 75% Complete",
            "",
            "┌─────────────┬─────────────┐",
            "│📊 Files     │📤 Uploaded  │",
            "│    12       │     9       │",
            "│RetroArch    │ 1.7MB synced│",
            "│PPSSPP       │✓ No conflicts│",
            "│2.3MB total  │             │",
            "└─────────────┴─────────────┘",
            "",
            "Currently uploading:",
            "📄 zelda_link_awakening.srm",
            "",
            "Recent uploads:",
            "✓ mario_world.srm (32 KB)",
            "✓ sonic_2.srm (16 KB)", 
            "✓ pokemon_red.sav (128 KB)",
            "",
            "[      Cancel Sync      ]"
        ],
        'screenshot-sync-progress.png'
    )
    
    print(f"\n🎉 Created 3 placeholder screenshots!")
    print("📂 Location: docs/screenshots/")
    print("\n💡 For real screenshots:")
    print("   1. Install APK on Android device")
    print("   2. Run: ./scripts/take-screenshots.sh")
    print("   3. Replace these placeholders")

if __name__ == '__main__':
    main()