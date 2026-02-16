#!/bin/bash

# EmuSaves Test Runner with Coverage Report
# Usage: ./run-tests.sh [--coverage] [--watch]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
COVERAGE=false
WATCH=false
OPEN_REPORT=false

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --coverage|-c)
            COVERAGE=true
            shift
            ;;
        --watch|-w)
            WATCH=true
            shift
            ;;
        --open|-o)
            OPEN_REPORT=true
            shift
            ;;
        --help|-h)
            echo "EmuSaves Test Runner"
            echo ""
            echo "Usage: $0 [options]"
            echo ""
            echo "Options:"
            echo "  -c, --coverage    Generate coverage report"
            echo "  -w, --watch       Watch for file changes and re-run tests"
            echo "  -o, --open        Open coverage report in browser"
            echo "  -h, --help        Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0                Run all unit tests"
            echo "  $0 --coverage     Run tests with coverage report"
            echo "  $0 -c -o          Run tests with coverage and open report"
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

echo -e "${BLUE}🧪 EmuSaves Test Runner${NC}"
echo "=================================="

# Function to run tests
run_tests() {
    echo -e "${YELLOW}📋 Running unit tests...${NC}"
    
    if [ "$COVERAGE" = true ]; then
        echo -e "${YELLOW}📊 With coverage reporting enabled${NC}"
        ./gradlew clean testDebugUnitTest jacocoTestReport --info
        
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✅ Tests passed with coverage report generated${NC}"
            
            # Show coverage summary
            if [ -f "app/build/reports/jacoco/jacocoTestReport/html/index.html" ]; then
                echo ""
                echo -e "${BLUE}📊 Coverage Report Generated:${NC}"
                echo "   HTML: app/build/reports/jacoco/jacocoTestReport/html/index.html"
                echo "   XML:  app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"
                
                if [ "$OPEN_REPORT" = true ]; then
                    if command -v xdg-open &> /dev/null; then
                        xdg-open app/build/reports/jacoco/jacocoTestReport/html/index.html
                    elif command -v open &> /dev/null; then
                        open app/build/reports/jacoco/jacocoTestReport/html/index.html
                    else
                        echo -e "${YELLOW}⚠️  Could not auto-open report. Open manually in browser.${NC}"
                    fi
                fi
            fi
        else
            echo -e "${RED}❌ Tests failed${NC}"
            return 1
        fi
    else
        ./gradlew testDebugUnitTest --info
        
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✅ Tests passed${NC}"
        else
            echo -e "${RED}❌ Tests failed${NC}"
            return 1
        fi
    fi
    
    # Show test results location
    echo ""
    echo -e "${BLUE}📋 Test Results:${NC}"
    echo "   HTML: app/build/reports/tests/testDebugUnitTest/index.html"
    echo "   XML:  app/build/test-results/testDebugUnitTest/"
}

# Function to run tests continuously
run_tests_watch() {
    echo -e "${YELLOW}👀 Watching for file changes... (Press Ctrl+C to stop)${NC}"
    echo ""
    
    # Use inotifywait if available, otherwise fall back to basic watch
    if command -v inotifywait &> /dev/null; then
        while true; do
            run_tests
            echo ""
            echo -e "${YELLOW}👀 Waiting for changes in src/...${NC}"
            inotifywait -r -e modify,create,delete src/ app/src/ --exclude='.*\.(tmp|swp)$' -q
            echo -e "${BLUE}🔄 Files changed, re-running tests...${NC}"
            echo ""
        done
    else
        echo -e "${YELLOW}⚠️  inotifywait not available. Install inotify-tools for better file watching.${NC}"
        echo -e "${YELLOW}   Using basic watch mode (checks every 2 seconds)${NC}"
        echo ""
        
        # Basic watch mode - check every 2 seconds
        last_hash=""
        while true; do
            current_hash=$(find src/ app/src/ -name "*.kt" -o -name "*.java" | xargs md5sum 2>/dev/null | md5sum)
            if [ "$current_hash" != "$last_hash" ]; then
                if [ -n "$last_hash" ]; then
                    echo -e "${BLUE}🔄 Files changed, re-running tests...${NC}"
                    echo ""
                    run_tests
                    echo ""
                fi
                last_hash="$current_hash"
            fi
            sleep 2
        done
    fi
}

# Make sure gradlew is executable
chmod +x gradlew

# Check if Android project exists
if [ ! -f "app/build.gradle" ]; then
    echo -e "${RED}❌ Error: Not in an Android project directory${NC}"
    echo "   Make sure you're in the EmuSaves project root"
    exit 1
fi

# Run tests
echo -e "${BLUE}🏗️  Building project...${NC}"
echo ""

if [ "$WATCH" = true ]; then
    run_tests_watch
else
    run_tests
fi

echo ""
echo -e "${GREEN}🎉 Test run complete!${NC}"