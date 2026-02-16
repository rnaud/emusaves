#!/usr/bin/env python3
"""
Generate PNG screenshots from HTML mockups
"""

import os
try:
    from PIL import Image, ImageDraw, ImageFont
    PIL_AVAILABLE = True
except ImportError:
    PIL_AVAILABLE = False
    print("PIL not available, creating simple text files instead")

def setup_driver():
    """Setup headless Chrome driver for screenshot capture"""
    options = Options()
    options.add_argument('--headless')
    options.add_argument('--no-sandbox')
    options.add_argument('--disable-dev-shm-usage')
    options.add_argument('--window-size=1200,800')
    
    try:
        driver = webdriver.Chrome(options=options)
        return driver
    except Exception as e:
        print(f"Chrome driver not available: {e}")
        return None

def capture_phone_screenshot(driver, phone_id, output_path):
    """Capture screenshot of specific phone mockup"""
    # Navigate to the HTML file
    html_path = os.path.abspath('create-realistic-mockups.html')
    driver.get(f'file://{html_path}')
    
    # Wait for page to load
    time.sleep(2)
    
    # Find the phone element
    phone_element = driver.find_element(By.ID, phone_id)
    
    # Take screenshot of the element
    png_data = phone_element.screenshot_as_png
    
    # Save to file
    with open(output_path, 'wb') as f:
        f.write(png_data)
    
    print(f"✓ Screenshot saved: {output_path}")

def create_placeholder_pngs():
    """Create placeholder PNG images if selenium is not available"""
    from PIL import Image, ImageDraw, ImageFont
    import io
    
    def create_phone_mockup(title, content_lines, filename):
        # Create a 300x600 image (phone dimensions)
        img = Image.new('RGB', (300, 600), color='#FAFAFA')
        draw = ImageDraw.Draw(img)
        
        try:
            # Try to use a system font
            font_large = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 18)
            font_medium = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 14)
            font_small = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 12)
        except:
            # Fallback to default font
            font_large = ImageFont.load_default()
            font_medium = ImageFont.load_default()
            font_small = ImageFont.load_default()
        
        # Status bar
        draw.rectangle([0, 0, 300, 24], fill='#1976D2')
        draw.text((12, 6), '9:41', fill='white', font=font_small)
        draw.text((260, 6), '100%', fill='white', font=font_small)
        
        # App bar
        draw.rectangle([0, 24, 300, 80], fill='#E3F2FD')
        draw.text((16, 45), 'EmuSaves', fill='#1976D2', font=font_large)
        
        # Title
        draw.text((20, 100), title, fill='#212121', font=font_medium)
        
        # Content
        y_pos = 140
        for line in content_lines:
            draw.text((20, y_pos), line, fill='#757575', font=font_small)
            y_pos += 25
        
        # Save
        img.save(filename, 'PNG')
        print(f"✓ Created placeholder: {filename}")
    
    # Create placeholder screenshots
    create_phone_mockup(
        "Home Screen",
        [
            "📊 Sync Status - Last sync: Feb 16, 01:15",
            "",
            "📁 Backup Folders (2)",
            "  🎮 RetroArch Saves",
            "  📱 PPSSPP Saves",
            "  [⭐ Quick Add] [+ Browse]",
            "",
            "🔧 Synology NAS - nas.local ✓",
            "",
            "⏰ Scheduled Sync - ON",
        ],
        'screenshot-home.png'
    )
    
    create_phone_mockup(
        "Quick Add Dialog",
        [
            "Select common emulator save locations:",
            "",
            "[🎮 Multi-System] [🕹️ Console] [📱 Handheld]",
            "",
            "🎮 RetroArch Saves",
            "   RetroArch • Battery saves and SRAM",
            "",
            "💾 RetroArch States", 
            "   RetroArch • Save states for all cores",
            "",
            "🎮 Lemuroid Saves ← SELECTED",
            "   Lemuroid • Multi-system saves",
        ],
        'screenshot-quick-add.png'
    )
    
    create_phone_mockup(
        "Sync Progress",
        [
            "🔄 Syncing to nas.local...",
            "████████████░░░░ 75% Complete",
            "",
            "📊 12 Files Found    📤 9 Uploaded",
            "   RetroArch, PPSSPP     1.7MB synced",
            "   2.3 MB total         ✓ No conflicts",
            "",
            "Currently uploading:",
            "📄 zelda_link_awakening.srm",
            "",
            "Recent uploads:",
            "✓ mario_world.srm (32 KB)",
            "✓ sonic_2.srm (16 KB)",
            "✓ pokemon_red.sav (128 KB)",
        ],
        'screenshot-sync-progress.png'
    )

def main():
    """Generate screenshots using available method"""
    print("🎮 EmuSaves Screenshot Generator")
    print("=" * 40)
    
    # Try selenium approach first
    driver = setup_driver()
    
    if driver:
        print("📸 Using Selenium WebDriver for high-quality screenshots...")
        try:
            capture_phone_screenshot(driver, 'home-screen', 'screenshot-home.png')
            capture_phone_screenshot(driver, 'quick-add-screen', 'screenshot-quick-add.png')
            capture_phone_screenshot(driver, 'sync-progress-screen', 'screenshot-sync-progress.png')
            driver.quit()
            print("\n✅ High-quality screenshots generated!")
        except Exception as e:
            print(f"❌ Selenium failed: {e}")
            driver.quit()
            create_placeholder_pngs()
    else:
        print("📱 Using PIL for placeholder screenshots...")
        create_placeholder_pngs()
    
    print("\n🎉 Screenshots ready!")
    print("📂 Files created in docs/screenshots/")
    print("💡 To get real screenshots, install the APK on Android and use:")
    print("   ./scripts/take-screenshots.sh")

if __name__ == '__main__':
    main()