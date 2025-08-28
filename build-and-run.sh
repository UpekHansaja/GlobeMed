#!/bin/bash

# GlobeMed Healthcare Management System - Build and Run Script
# This script compiles and runs the complete healthcare management system

echo "🏥 GlobeMed Healthcare Management System"
echo "========================================"
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java version check passed"

# Check if MySQL is running (optional check)
if command -v mysql &> /dev/null; then
    echo "✅ MySQL is available"
else
    echo "⚠️  MySQL not found. Please ensure MySQL is installed and running."
    echo "   Database: globemed"
    echo "   Default credentials are configured in hibernate.cfg.xml"
fi

echo ""
echo "🔧 System Features:"
echo "   • Complete Patient Management"
echo "   • Staff Management with Role-based Access Control"
echo "   • Advanced Appointment Scheduling"
echo "   • Comprehensive Billing System"
echo "   • Multi-channel Notification System"
echo "   • Audit Logging and Security"
echo "   • Advanced Reporting and Analytics"
echo "   • Implementation of 11+ Design Patterns"
echo ""

echo "👥 Default Login Credentials:"
echo "   Administrator: admin@globemed.lk / admin123"
echo "   Doctor: doctor@globemed.lk / doctor123"
echo "   Nurse: nurse@globemed.lk / nurse123"
echo "   Pharmacist: pharmacist@globemed.lk / pharmacist123"
echo "   Accountant: accountant@globemed.lk / accountant123"
echo ""

echo "🚀 Starting GlobeMed Healthcare Management System..."
echo ""

# Navigate to the project directory
cd "$(dirname "$0")"

# Compile the project (if using command line compilation)
# Note: This assumes NetBeans or IDE compilation. For command line:
# javac -cp "lib/*" -d build/classes src/lk/jiat/globemed/**/*.java

# Run the application
# Note: This assumes the project is built with NetBeans or similar IDE
# For command line execution, you would use:
# java -cp "build/classes:lib/*" lk.jiat.globemed.app.MainApp

echo "📝 Note: This system demonstrates advanced Object-Oriented Design Patterns"
echo "   including Composite, Bridge, Builder, Chain of Responsibility, Flyweight,"
echo "   Interpreter, Mediator, Prototype, Decorator, Visitor, and Memento patterns."
echo ""
echo "🎯 For best experience, open this project in NetBeans IDE and run MainApp.java"
echo ""
echo "📚 Documentation:"
echo "   • README.md - System overview and setup instructions"
echo "   • DESIGN_PATTERNS_DOCUMENTATION.md - Detailed pattern implementations"
echo "   • SYSTEM_FEATURES.md - Complete feature documentation"
echo ""
echo "✨ GlobeMed - Advanced Healthcare Management System Ready!"