#!/usr/bin/env python3
"""
Create PNG screenshots from our SVG files using available tools
"""
import os
import subprocess
import sys

def convert_svg_to_png():
    """Convert SVG screenshots to PNG using available tools"""
    
    svg_files = [
        ('docs/screenshots/realistic-home-screen.svg', 'docs/screenshots/screenshot-home.png'),
        ('docs/screenshots/realistic-quick-add-dialog.svg', 'docs/screenshots/screenshot-quick-add.png'), 
        ('docs/screenshots/realistic-sync-progress.svg', 'docs/screenshots/screenshot-sync-progress.png'),
        ('docs/screenshots/realistic-synology-config.svg', 'docs/screenshots/screenshot-synology-config.png'),
    ]
    
    conversion_methods = [
        # Try rsvg-convert (librsvg)
        lambda svg, png: ['rsvg-convert', '-w', '320', '-h', '640', svg, '-o', png],
        # Try inkscape
        lambda svg, png: ['inkscape', '--export-type=png', '--export-width=320', '--export-height=640', '--export-filename=' + png, svg],
        # Try ImageMagick convert
        lambda svg, png: ['convert', '-background', 'transparent', '-size', '320x640', svg, png],
        # Try cairosvg (if available)
        lambda svg, png: ['cairosvg', svg, '-o', png, '-W', '320', '-H', '640'],
    ]
    
    success_count = 0
    
    for svg_file, png_file in svg_files:
        if not os.path.exists(svg_file):
            print(f"❌ SVG file not found: {svg_file}")
            continue
            
        # Try each conversion method
        converted = False
        for method in conversion_methods:
            try:
                cmd = method(svg_file, png_file)
                result = subprocess.run(cmd, capture_output=True, text=True, timeout=30)
                if result.returncode == 0:
                    print(f"✅ Converted {svg_file} → {png_file}")
                    success_count += 1
                    converted = True
                    break
                else:
                    continue
            except (subprocess.TimeoutExpired, FileNotFoundError, subprocess.CalledProcessError):
                continue
        
        if not converted:
            print(f"❌ Failed to convert {svg_file} (no working conversion tool found)")
    
    return success_count

def install_conversion_tools():
    """Try to install SVG conversion tools"""
    install_commands = [
        'apt-get update && apt-get install -y librsvg2-bin',  # rsvg-convert
        'apt-get install -y inkscape',  # inkscape
        'apt-get install -y imagemagick',  # convert
        'pip3 install cairosvg',  # cairosvg
    ]
    
    print("🔧 Trying to install SVG conversion tools...")
    
    for cmd in install_commands:
        try:
            print(f"Trying: {cmd}")
            result = subprocess.run(cmd, shell=True, capture_output=True, text=True, timeout=60)
            if result.returncode == 0:
                print(f"✅ Successfully installed tool")
                return True
            else:
                print(f"❌ Installation failed: {result.stderr[:200]}")
        except (subprocess.TimeoutExpired, subprocess.CalledProcessError) as e:
            print(f"❌ Installation error: {e}")
    
    return False

def create_simple_html_screenshots():
    """Create HTML files that can be screenshot manually"""
    
    html_template = """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=320, initial-scale=1.0">
        <title>EmuSaves - {title}</title>
        <style>
            body {{ margin: 0; padding: 0; width: 320px; height: 640px; overflow: hidden; }}
            iframe {{ width: 320px; height: 640px; border: none; }}
        </style>
    </head>
    <body>
        <div style="width: 320px; height: 640px; transform-origin: top left;">
            {svg_content}
        </div>
    </body>
    </html>
    """
    
    screenshots = [
        ('realistic-home-screen.svg', 'Home Screen', 'screenshot-home.html'),
        ('realistic-quick-add-dialog.svg', 'Quick Add Dialog', 'screenshot-quick-add.html'),
        ('realistic-sync-progress.svg', 'Sync Progress', 'screenshot-sync-progress.html'),
        ('realistic-synology-config.svg', 'Synology Config', 'screenshot-synology-config.html'),
    ]
    
    os.makedirs('docs/screenshots', exist_ok=True)
    
    for svg_name, title, html_name in screenshots:
        svg_path = f'docs/screenshots/{svg_name}'
        html_path = f'docs/screenshots/{html_name}'
        
        if os.path.exists(svg_path):
            with open(svg_path, 'r') as f:
                svg_content = f.read()
            
            html_content = html_template.format(title=title, svg_content=svg_content)
            
            with open(html_path, 'w') as f:
                f.write(html_content)
            
            print(f"✅ Created {html_path}")
    
    print(f"\n📱 HTML screenshot files created in docs/screenshots/")
    print(f"💡 You can open these in a browser and screenshot them manually:")
    for _, title, html_name in screenshots:
        print(f"   file://{os.path.abspath('docs/screenshots/' + html_name)}")

def main():
    """Main function"""
    print("📸 EmuSaves PNG Screenshot Creator")
    print("=" * 40)
    
    # First try converting existing SVG files
    success_count = convert_svg_to_png()
    
    if success_count == 0:
        print("\n🔧 No SVG conversion tools found. Trying to install...")
        if install_conversion_tools():
            success_count = convert_svg_to_png()
    
    if success_count == 0:
        print("\n📱 Creating HTML files for manual screenshot capture...")
        create_simple_html_screenshots()
        print("\n💡 Manual screenshot process:")
        print("   1. Open the HTML files in a browser")
        print("   2. Set browser window to exactly 320x640 pixels")
        print("   3. Take screenshots and save as PNG")
    else:
        print(f"\n🎉 Successfully created {success_count} PNG screenshots!")
    
    print(f"\n📂 Screenshots available in docs/screenshots/")

if __name__ == '__main__':
    main()