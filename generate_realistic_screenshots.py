#!/usr/bin/env python3
"""
Generate realistic screenshots of EmuSaves app based on actual code structure
"""

try:
    from PIL import Image, ImageDraw, ImageFont
    PIL_AVAILABLE = True
except ImportError:
    PIL_AVAILABLE = False

import os

def create_android_phone_frame(content_image, filename):
    """Create a phone frame around the content"""
    if not PIL_AVAILABLE:
        return False
        
    # Create phone frame (Google Pixel style)
    phone_width = 340
    phone_height = 680
    
    # Phone background (black frame)
    phone = Image.new('RGBA', (phone_width, phone_height), (0, 0, 0, 255))
    draw = ImageDraw.Draw(phone)
    
    # Rounded rectangle for screen area
    screen_x, screen_y = 20, 20
    screen_width = phone_width - 40
    screen_height = phone_height - 40
    
    # Screen background
    screen_bg = Image.new('RGBA', (screen_width, screen_height), (250, 250, 250, 255))
    
    # Resize content to fit screen
    content_resized = content_image.resize((screen_width, screen_height), Image.Resampling.LANCZOS)
    
    # Composite screen onto phone
    phone.paste(screen_bg, (screen_x, screen_y))
    phone.paste(content_resized, (screen_x, screen_y), content_resized if content_resized.mode == 'RGBA' else None)
    
    # Add phone details (home button, etc.)
    # Home indicator
    home_width = 60
    home_height = 4
    home_x = (phone_width - home_width) // 2
    home_y = phone_height - 15
    draw.rounded_rectangle([home_x, home_y, home_x + home_width, home_y + home_height], 
                          radius=2, fill=(100, 100, 100, 255))
    
    phone.save(filename, 'PNG')
    return True

def create_home_screen():
    """Create realistic home screen screenshot"""
    if not PIL_AVAILABLE:
        return False
        
    width, height = 300, 600
    img = Image.new('RGB', (width, height), (250, 250, 250))
    draw = ImageDraw.Draw(img)
    
    try:
        # Load system fonts
        font_large = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 20)
        font_medium = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 16)
        font_small = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 14)
        font_tiny = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 12)
    except:
        font_large = font_medium = font_small = font_tiny = ImageFont.load_default()
    
    # Colors (Material Design 3)
    primary_color = (25, 118, 210)      # #1976D2
    surface_color = (255, 255, 255)     # White
    on_surface = (33, 33, 33)           # Dark gray
    outline = (224, 224, 224)           # Light gray
    success_color = (76, 175, 80)       # Green
    
    y_pos = 0
    
    # Status bar
    draw.rectangle([0, y_pos, width, y_pos + 24], fill=primary_color)
    draw.text((12, y_pos + 6), '9:41', fill='white', font=font_tiny)
    draw.text((width - 50, y_pos + 6), '100%', fill='white', font=font_tiny)
    y_pos += 24
    
    # App bar  
    draw.rectangle([0, y_pos, width, y_pos + 56], fill=(227, 242, 253))
    draw.text((16, y_pos + 18), 'EmuSaves', fill=primary_color, font=font_large)
    y_pos += 72
    
    # Sync Status Card
    card_margin = 16
    card_width = width - (card_margin * 2)
    card_height = 120
    
    # Card background with shadow effect
    draw.rectangle([card_margin + 2, y_pos + 2, card_margin + card_width + 2, y_pos + card_height + 2], 
                  fill=(200, 200, 200, 100))  # Shadow
    draw.rectangle([card_margin, y_pos, card_margin + card_width, y_pos + card_height], 
                  fill=surface_color, outline=outline)
    
    # Card content
    card_x = card_margin + 16
    card_y = y_pos + 16
    
    # Title and status indicator
    draw.text((card_x, card_y), 'Sync Status', fill=on_surface, font=font_medium)
    draw.ellipse([card_x + card_width - 40, card_y - 2, card_x + card_width - 32, card_y + 6], fill=success_color)
    
    # Last sync info
    card_y += 24
    draw.text((card_x, card_y), 'Last sync: Feb 16, 01:15', fill=(117, 117, 117), font=font_small)
    
    # Sync button
    card_y += 30
    button_width = card_width - 32
    button_height = 36
    draw.rectangle([card_x, card_y, card_x + button_width, card_y + button_height], 
                  fill=primary_color, outline=primary_color)
    
    # Center the button text
    button_text = 'Sync Now'
    bbox = draw.textbbox((0, 0), button_text, font=font_small)
    text_width = bbox[2] - bbox[0]
    text_x = card_x + (button_width - text_width) // 2
    draw.text((text_x, card_y + 10), button_text, fill='white', font=font_small)
    
    y_pos += card_height + 16
    
    # Folders Card
    card_height = 160
    draw.rectangle([card_margin + 2, y_pos + 2, card_margin + card_width + 2, y_pos + card_height + 2], 
                  fill=(200, 200, 200, 100))  # Shadow
    draw.rectangle([card_margin, y_pos, card_margin + card_width, y_pos + card_height], 
                  fill=surface_color, outline=outline)
    
    card_y = y_pos + 16
    draw.text((card_x, card_y), 'Backup Folders', fill=on_surface, font=font_medium)
    
    # Folder items
    card_y += 30
    # RetroArch folder
    draw.ellipse([card_x, card_y - 2, card_x + 16, card_y + 14], fill=success_color)
    draw.text((card_x + 24, card_y), 'RetroArch Saves', fill=on_surface, font=font_small)
    draw.ellipse([card_x + card_width - 56, card_y - 2, card_x + card_width - 40, card_y + 14], 
                fill=(255, 87, 34), outline=(255, 87, 34))
    draw.text((card_x + card_width - 50, card_y + 2), '×', fill='white', font=font_small)
    
    card_y += 28
    # PPSSPP folder
    draw.ellipse([card_x, card_y - 2, card_x + 16, card_y + 14], fill=(33, 150, 243))
    draw.text((card_x + 24, card_y), 'PPSSPP Saves', fill=on_surface, font=font_small)
    draw.ellipse([card_x + card_width - 56, card_y - 2, card_x + card_width - 40, card_y + 14], 
                fill=(255, 87, 34), outline=(255, 87, 34))
    draw.text((card_x + card_width - 50, card_y + 2), '×', fill='white', font=font_small)
    
    # Action buttons
    card_y += 40
    button_width = (card_width - 40) // 2
    
    # Quick Add button (filled tonal)
    draw.rectangle([card_x, card_y, card_x + button_width, card_y + 36], 
                  fill=(227, 242, 253), outline=primary_color)
    quick_add_text = '⭐ Quick Add'
    bbox = draw.textbbox((0, 0), quick_add_text, font=font_tiny)
    text_width = bbox[2] - bbox[0]
    text_x = card_x + (button_width - text_width) // 2
    draw.text((text_x, card_y + 11), quick_add_text, fill=primary_color, font=font_tiny)
    
    # Browse button (outlined)
    button_x = card_x + button_width + 8
    draw.rectangle([button_x, card_y, button_x + button_width, card_y + 36], 
                  fill='white', outline=(117, 117, 117))
    browse_text = '+ Browse'
    bbox = draw.textbbox((0, 0), browse_text, font=font_tiny)
    text_width = bbox[2] - bbox[0]
    text_x = button_x + (button_width - text_width) // 2
    draw.text((text_x, card_y + 11), browse_text, fill=(117, 117, 117), font=font_tiny)
    
    y_pos += card_height + 16
    
    # Synology Card
    card_height = 100
    draw.rectangle([card_margin + 2, y_pos + 2, card_margin + card_width + 2, y_pos + card_height + 2], 
                  fill=(200, 200, 200, 100))  # Shadow
    draw.rectangle([card_margin, y_pos, card_margin + card_width, y_pos + card_height], 
                  fill=surface_color, outline=outline)
    
    card_y = y_pos + 16
    draw.text((card_x, card_y), 'Synology NAS', fill=on_surface, font=font_medium)
    draw.ellipse([card_x + card_width - 40, card_y - 2, card_x + card_width - 32, card_y + 6], fill=success_color)
    
    card_y += 24
    draw.text((card_x, card_y), 'nas.local → /Drive/EmulatorBackups', fill=(117, 117, 117), font=font_tiny)
    
    card_y += 24
    draw.rectangle([card_x, card_y, card_x + card_width - 32, card_y + 32], 
                  fill='white', outline=(117, 117, 117))
    config_text = 'Update Configuration'
    bbox = draw.textbbox((0, 0), config_text, font=font_tiny)
    text_width = bbox[2] - bbox[0]
    text_x = card_x + ((card_width - 32) - text_width) // 2
    draw.text((text_x, card_y + 10), config_text, fill=(117, 117, 117), font=font_tiny)
    
    return img

def create_quick_add_dialog():
    """Create Quick Add dialog screenshot"""
    if not PIL_AVAILABLE:
        return False
        
    width, height = 300, 600
    img = Image.new('RGB', (width, height), (250, 250, 250))
    draw = ImageDraw.Draw(img)
    
    try:
        font_large = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 18)
        font_medium = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 14)
        font_small = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 12)
        font_tiny = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 11)
    except:
        font_large = font_medium = font_small = font_tiny = ImageFont.load_default()
    
    # Colors
    primary_color = (25, 118, 210)
    surface_color = (255, 255, 255)
    on_surface = (33, 33, 33)
    outline = (224, 224, 224)
    
    y_pos = 0
    
    # Status bar and app bar (dimmed background)
    draw.rectangle([0, 0, width, height], fill=(0, 0, 0, 128))  # Overlay
    
    # Dialog
    dialog_margin = 20
    dialog_width = width - (dialog_margin * 2)
    dialog_height = 440
    dialog_y = 80
    
    # Dialog shadow
    draw.rectangle([dialog_margin + 4, dialog_y + 4, dialog_margin + dialog_width + 4, 
                   dialog_y + dialog_height + 4], fill=(100, 100, 100))
    
    # Dialog background
    draw.rectangle([dialog_margin, dialog_y, dialog_margin + dialog_width, dialog_y + dialog_height], 
                  fill=surface_color, outline=outline)
    
    dialog_x = dialog_margin + 16
    current_y = dialog_y + 24
    
    # Title
    draw.text((dialog_x, current_y), 'Quick Add Emulator Folders', fill=on_surface, font=font_large)
    current_y += 32
    
    # Subtitle
    draw.text((dialog_x, current_y), 'Select common emulator save locations:', 
              fill=(117, 117, 117), font=font_small)
    current_y += 24
    
    # Category chips
    chip_y = current_y
    chip_height = 28
    
    # Multi-System chip (selected)
    chip_width = 85
    draw.rectangle([dialog_x, chip_y, dialog_x + chip_width, chip_y + chip_height], 
                  fill=(227, 242, 253), outline=primary_color)
    draw.text((dialog_x + 8, chip_y + 8), '🎮 Multi', fill=primary_color, font=font_tiny)
    
    # Console chip
    chip_x = dialog_x + chip_width + 8
    chip_width = 70
    draw.rectangle([chip_x, chip_y, chip_x + chip_width, chip_y + chip_height], 
                  fill=(245, 245, 245), outline=outline)
    draw.text((chip_x + 8, chip_y + 8), '🕹️ Console', fill=(117, 117, 117), font=font_tiny)
    
    # Handheld chip
    chip_x += chip_width + 8
    chip_width = 80
    draw.rectangle([chip_x, chip_y, chip_x + chip_width, chip_y + chip_height], 
                  fill=(245, 245, 245), outline=outline)
    draw.text((chip_x + 8, chip_y + 8), '📱 Handheld', fill=(117, 117, 117), font=font_tiny)
    
    current_y += chip_height + 16
    
    # Location items
    item_height = 56
    item_width = dialog_width - 32
    
    locations = [
        ('🎮', 'RetroArch Saves', 'RetroArch', 'Battery saves and SRAM files', False),
        ('💾', 'RetroArch States', 'RetroArch', 'Save states for all cores', False),
        ('🎮', 'Lemuroid Saves', 'Lemuroid', 'Multi-system emulator saves', True),  # Selected
    ]
    
    for icon, name, emulator, description, selected in locations:
        # Item background
        if selected:
            draw.rectangle([dialog_x, current_y, dialog_x + item_width, current_y + item_height], 
                          fill=(227, 242, 253), outline=primary_color, width=2)
            text_color = primary_color
            desc_color = (21, 101, 192)
        else:
            draw.rectangle([dialog_x, current_y, dialog_x + item_width, current_y + item_height], 
                          fill=(248, 249, 250), outline=outline)
            text_color = on_surface
            desc_color = (117, 117, 117)
        
        # Icon
        draw.text((dialog_x + 8, current_y + 18), icon, font=font_medium)
        
        # Text content
        text_x = dialog_x + 32
        draw.text((text_x, current_y + 12), name, fill=text_color, font=font_small)
        draw.text((text_x, current_y + 28), emulator, fill=primary_color, font=font_tiny)
        draw.text((text_x, current_y + 42), description, fill=desc_color, font=font_tiny)
        
        # Arrow
        if selected:
            draw.text((dialog_x + item_width - 20, current_y + 24), '→', fill=primary_color, font=font_medium)
        else:
            draw.text((dialog_x + item_width - 20, current_y + 24), '→', fill=(117, 117, 117), font=font_medium)
        
        current_y += item_height + 4
    
    # Close button
    button_y = dialog_y + dialog_height - 60
    button_width = 64
    button_x = dialog_margin + dialog_width - button_width - 16
    draw.rectangle([button_x, button_y, button_x + button_width, button_y + 36], 
                  fill=primary_color)
    close_text = 'Close'
    bbox = draw.textbbox((0, 0), close_text, font=font_small)
    text_width = bbox[2] - bbox[0]
    text_x = button_x + (button_width - text_width) // 2
    draw.text((text_x, button_y + 10), close_text, fill='white', font=font_small)
    
    return img

def create_sync_progress():
    """Create sync progress screenshot"""  
    if not PIL_AVAILABLE:
        return False
        
    width, height = 300, 600
    img = Image.new('RGB', (width, height), (250, 250, 250))
    draw = ImageDraw.Draw(img)
    
    try:
        font_large = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 20)
        font_medium = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 16)
        font_small = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 14)
        font_tiny = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf', 12)
    except:
        font_large = font_medium = font_small = font_tiny = ImageFont.load_default()
    
    # Colors
    primary_color = (25, 118, 210)
    surface_color = (255, 255, 255)
    on_surface = (33, 33, 33)
    outline = (224, 224, 224)
    success_color = (76, 175, 80)
    warning_color = (255, 152, 0)
    
    y_pos = 0
    
    # Status bar
    draw.rectangle([0, y_pos, width, y_pos + 24], fill=primary_color)
    draw.text((12, y_pos + 6), '9:43', fill='white', font=font_tiny)
    draw.text((width - 50, y_pos + 6), '95%', fill='white', font=font_tiny)
    y_pos += 24
    
    # App bar
    draw.rectangle([0, y_pos, width, y_pos + 56], fill=(227, 242, 253))
    draw.text((16, y_pos + 18), 'EmuSaves', fill=primary_color, font=font_large)
    y_pos += 72
    
    # Active Sync Status Card (highlighted border)
    card_margin = 16
    card_width = width - (card_margin * 2)
    card_height = 400
    
    # Card with success border
    draw.rectangle([card_margin + 2, y_pos + 2, card_margin + card_width + 2, y_pos + card_height + 2], 
                  fill=(200, 200, 200, 100))
    draw.rectangle([card_margin, y_pos, card_margin + card_width, y_pos + card_height], 
                  fill=surface_color, outline=success_color, width=2)
    
    card_x = card_margin + 16
    card_y = y_pos + 16
    
    # Title with spinner
    draw.text((card_x, card_y), 'Syncing...', fill=on_surface, font=font_medium)
    
    # Simple spinner (rotating circle)
    spinner_x = card_x + card_width - 60
    draw.arc([spinner_x, card_y - 4, spinner_x + 24, card_y + 20], start=0, end=90, 
             fill=success_color, width=2)
    
    card_y += 28
    draw.text((card_x, card_y), 'Uploading to nas.local...', fill=success_color, font=font_small)
    
    # Progress bar
    card_y += 24
    progress_width = card_width - 32
    progress_height = 4
    draw.rectangle([card_x, card_y, card_x + progress_width, card_y + progress_height], 
                  fill=(224, 224, 224))
    # 75% progress
    progress_fill = int(progress_width * 0.75)
    draw.rectangle([card_x, card_y, card_x + progress_fill, card_y + progress_height], 
                  fill=success_color)
    
    card_y += 16
    draw.text((card_x + progress_width // 2 - 40, card_y), '75% Complete', 
              fill=success_color, font=font_small)
    
    # Stats boxes
    card_y += 32
    stat_width = (card_width - 40) // 2
    stat_height = 80
    
    # Files Found stat
    draw.rectangle([card_x, card_y, card_x + stat_width, card_y + stat_height], 
                  fill=(232, 245, 233), outline=success_color)
    draw.text((card_x + stat_width // 2 - 8, card_y + 12), '12', 
              fill=success_color, font=font_large)
    draw.text((card_x + stat_width // 2 - 30, card_y + 36), 'Files Found', 
              fill=success_color, font=font_tiny)
    draw.text((card_x + 8, card_y + 52), 'RetroArch, PPSSPP', fill=(117, 117, 117), font=font_tiny)
    draw.text((card_x + 8, card_y + 66), '2.3 MB total', fill=(117, 117, 117), font=font_tiny)
    
    # Uploaded stat  
    stat_x = card_x + stat_width + 8
    draw.rectangle([stat_x, card_y, stat_x + stat_width, card_y + stat_height], 
                  fill=(227, 242, 253), outline=primary_color)
    draw.text((stat_x + stat_width // 2 - 8, card_y + 12), '9', 
              fill=primary_color, font=font_large)
    draw.text((stat_x + stat_width // 2 - 20, card_y + 36), 'Uploaded', 
              fill=primary_color, font=font_tiny)
    draw.text((stat_x + 8, card_y + 52), '1.7 MB synced', fill=(117, 117, 117), font=font_tiny)
    draw.text((stat_x + 8, card_y + 66), '✓ No conflicts', fill=success_color, font=font_tiny)
    
    # Current file
    card_y += stat_height + 16
    draw.text((card_x, card_y), 'Currently uploading:', fill=(117, 117, 117), font=font_small)
    card_y += 20
    
    # Current file box
    draw.rectangle([card_x, card_y, card_x + card_width - 32, card_y + 32], 
                  fill=(255, 243, 224), outline=warning_color)
    draw.text((card_x + 8, card_y + 10), 'zelda_link_awakening.srm', 
              fill=(230, 81, 0), font=font_tiny)
    draw.text((card_x + card_width - 60, card_y + 10), '47%', fill=warning_color, font=font_tiny)
    
    # Recent uploads
    card_y += 48
    draw.text((card_x, card_y), 'Recent uploads:', fill=(117, 117, 117), font=font_small)
    card_y += 20
    
    recent_files = [
        '✓ mario_world.srm (32 KB)',
        '✓ sonic_2.srm (16 KB)', 
        '✓ pokemon_red.sav (128 KB)'
    ]
    
    for file_text in recent_files:
        draw.text((card_x, card_y), file_text, fill=success_color, font=font_tiny)
        card_y += 16
    
    # Cancel button
    card_y += 16
    draw.rectangle([card_x, card_y, card_x + card_width - 32, card_y + 40], 
                  fill=(255, 235, 238), outline=(244, 67, 54))
    cancel_text = 'Cancel Sync'
    bbox = draw.textbbox((0, 0), cancel_text, font=font_small)
    text_width = bbox[2] - bbox[0]
    text_x = card_x + ((card_width - 32) - text_width) // 2
    draw.text((text_x, card_y + 12), cancel_text, fill=(211, 47, 47), font=font_small)
    
    return img

def main():
    """Generate all realistic screenshots"""
    print("🎮 Generating realistic EmuSaves screenshots...")
    
    if not PIL_AVAILABLE:
        print("❌ PIL not available. Install with: pip install pillow")
        return
    
    # Create output directory
    os.makedirs('docs/screenshots', exist_ok=True)
    
    # Generate screenshots
    screenshots = [
        ('Home Screen', create_home_screen, 'screenshot-home-realistic.png'),
        ('Quick Add Dialog', create_quick_add_dialog, 'screenshot-quick-add-realistic.png'), 
        ('Sync Progress', create_sync_progress, 'screenshot-sync-progress-realistic.png'),
    ]
    
    success_count = 0
    for name, creator_func, filename in screenshots:
        print(f"📱 Creating {name}...")
        
        try:
            screenshot = creator_func()
            if screenshot:
                # Save with phone frame
                filepath = f'docs/screenshots/{filename}'
                create_android_phone_frame(screenshot, filepath)
                print(f"✅ {name} saved as {filepath}")
                success_count += 1
            else:
                print(f"❌ Failed to create {name}")
        except Exception as e:
            print(f"❌ Error creating {name}: {e}")
    
    print(f"\n🎉 Generated {success_count}/{len(screenshots)} realistic screenshots!")
    print("📂 Screenshots saved in docs/screenshots/")
    
    if success_count > 0:
        print("\n💡 These screenshots show:")
        print("  • Material Design 3 interface")
        print("  • ⭐ Quick Add button next to Browse button")  
        print("  • Quick Add dialog with emulator categories")
        print("  • Real sync progress with file details")
        print("  • Accurate colors and typography")

if __name__ == '__main__':
    main()