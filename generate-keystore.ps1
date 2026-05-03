# Script to generate release keystore for MCTB Auto-Reply app
# Run this script and follow the prompts

Write-Host "=== MCTB Auto-Reply Release Keystore Generator ===" -ForegroundColor Green
Write-Host ""
Write-Host "This will create a keystore file for signing your release APKs." -ForegroundColor Yellow
Write-Host "IMPORTANT: Remember the passwords you enter - you'll need them for every release!" -ForegroundColor Red
Write-Host ""

# Set keystore location
$keystorePath = "C:\Users\Aarons\mctb\mctb-release-key.jks"

# Prompt for passwords
$storePassword = Read-Host "Enter keystore password (min 6 characters)" -AsSecureString
$storePasswordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($storePassword))

$keyPassword = Read-Host "Enter key password (min 6 characters)" -AsSecureString
$keyPasswordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($keyPassword))

# Prompt for certificate information
Write-Host ""
Write-Host "Certificate Information (press Enter to use defaults):" -ForegroundColor Cyan
$firstName = Read-Host "First and Last Name [MCTB Developer]"
if ([string]::IsNullOrWhiteSpace($firstName)) { $firstName = "MCTB Developer" }

$orgUnit = Read-Host "Organizational Unit [Development]"
if ([string]::IsNullOrWhiteSpace($orgUnit)) { $orgUnit = "Development" }

$org = Read-Host "Organization [MCTB]"
if ([string]::IsNullOrWhiteSpace($org)) { $org = "MCTB" }

$city = Read-Host "City or Locality [Unknown]"
if ([string]::IsNullOrWhiteSpace($city)) { $city = "Unknown" }

$state = Read-Host "State or Province [Unknown]"
if ([string]::IsNullOrWhiteSpace($state)) { $state = "Unknown" }

$country = Read-Host "Country Code (2 letters) [US]"
if ([string]::IsNullOrWhiteSpace($country)) { $country = "US" }

# Build dname
$dname = "CN=$firstName, OU=$orgUnit, O=$org, L=$city, ST=$state, C=$country"

Write-Host ""
Write-Host "Generating keystore..." -ForegroundColor Yellow

# Generate keystore
$command = "keytool -genkeypair -v -keystore `"$keystorePath`" -alias mctb-release -keyalg RSA -keysize 2048 -validity 10000 -storepass `"$storePasswordPlain`" -keypass `"$keyPasswordPlain`" -dname `"$dname`""

try {
    Invoke-Expression $command

    Write-Host ""
    Write-Host "✅ Keystore created successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Keystore location: $keystorePath" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "IMPORTANT - Save these credentials securely:" -ForegroundColor Red
    Write-Host "  Keystore Password: $storePasswordPlain"
    Write-Host "  Key Password: $keyPasswordPlain"
    Write-Host "  Key Alias: mctb-release"
    Write-Host ""
    Write-Host "⚠️  NEVER commit the keystore file or passwords to git!" -ForegroundColor Yellow
    Write-Host "⚠️  Store them in a password manager or secure location!" -ForegroundColor Yellow
    Write-Host ""

    # Create a credentials template file
    $credsFile = "C:\Users\Aarons\mctb\keystore-credentials.txt"
    @"
MCTB Auto-Reply Release Keystore Credentials
=============================================
DO NOT COMMIT THIS FILE TO GIT!
Add to .gitignore immediately!

Keystore File: mctb-release-key.jks
Keystore Password: $storePasswordPlain
Key Alias: mctb-release
Key Password: $keyPasswordPlain

Certificate DN: $dname
Validity: 10000 days (approx 27 years)
"@ | Out-File -FilePath $credsFile -Encoding UTF8

    Write-Host "Credentials saved to: $credsFile" -ForegroundColor Cyan
    Write-Host "Move this file to a secure location and delete it from the project!" -ForegroundColor Yellow

} catch {
    Write-Host ""
    Write-Host "❌ Error generating keystore: $_" -ForegroundColor Red
}

# Clear sensitive variables
$storePasswordPlain = $null
$keyPasswordPlain = $null
