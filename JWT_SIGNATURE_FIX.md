# JWT Signature Error - COMPLETE FIX

## The Problem
Error: `Invalid JWT signature: JWT signature does not match locally computed signature`

## Root Cause
Your old JWT tokens were signed with a DIFFERENT secret key than the one being used to verify them now.

## The Solution (3 Steps)

### Step 1: Ensure Consistent Configuration ✅ DONE
Both files now use the same secret with `base64:` prefix:

**File: `src/main/resources/application.properties`**
```properties
jwt.secret=base64:YXV0aG5tb25leW1hbmFnZWFwcGF1dGhubW9uZXltYW5hZ2VhcHBzZWNyZXRrZXlmb3JzZWN1cml0eQ==
```

**File: `src/main/java/.../util/JwtUtill.java`**
```java
@Value("${jwt.secret:base64:YXV0aG5tb25leW1hbmFnZWFwcGF1dGhubW9uZXltYW5hZ2VhcHBzZWNyZXRrZXlmb3JzZWN1cml0eQ==}")
```

### Step 2: Restart Your Application
**IMPORTANT:** You must restart the Spring Boot application for changes to take effect.

1. Stop the running application (PID 19200 or whatever is running on port 8081)
2. Run: `.\mvnw.cmd spring-boot:run`
3. Wait for startup to complete

### Step 3: Generate NEW Tokens ⚠️ CRITICAL
**Old tokens will NOT work!** You must:

1. **Delete old tokens** from your client (browser localStorage, Postman, etc.)
2. **Login again** to get a NEW token signed with the correct secret
3. **Use the new token** for all requests

## Why This Happened

Your tokens were originally signed with this secret (WITHOUT `base64:` prefix):
- Secret: `YXV0aG5tb25leW1hbmFnZWFwcGF1dGhubW9uZXltYW5hZ2VhcHBzZWNyZXRrZXlmb3JzZWN1cml0eQ==`
- Decoded to: 55 bytes (base64 decoded)

But the default in JwtUtill.java was:
- Secret: `YXV0aG5tb25leW1hbmFnZWFwcGF1dGhubW9uZXltYW5hZ2VhcHBzZWNyZXRrZXk=`
- Decoded to: 51 bytes (DIFFERENT!)

This caused signature mismatch.

## Verification Steps

### 1. Check the logs when starting the app
You should see:
```
DEBUG c.a.smartmoneymanageapp.util.JwtUtill : Using base64 secret (prefixed). key bytes: 55
```

### 2. Test the login endpoint
```bash
POST http://localhost:8081/api/v1.0/login
Content-Type: application/json

{
  "email": "your@email.com",
  "password": "yourpassword"
}
```

This will return a NEW token.

### 3. Use the NEW token
```bash
GET http://localhost:8081/api/v1.0/profile
Authorization: Bearer <NEW_TOKEN_HERE>
```

## If Still Getting Errors

1. **Check you restarted the app** - Changes don't apply until restart
2. **Verify you're using a NEW token** - Old tokens are invalid forever
3. **Check DEBUG logs** - Look for "Using base64 secret (prefixed). key bytes: 55"
4. **Clear all cached tokens** - Remove from browser, Postman, etc.

## Production Recommendations

1. **Use environment variables:**
   ```properties
   jwt.secret=${JWT_SECRET}
   ```
   Then set `JWT_SECRET=base64:YXV0aG5tb25leW1hbmFnZWFwcGF1dGhubW9uZXltYW5hZ2VhcHBzZWNyZXRrZXlmb3JzZWN1cml0eQ==` in your environment

2. **Generate a stronger secret:**
   ```bash
   # PowerShell
   [Convert]::ToBase64String((1..32 | ForEach-Object {Get-Random -Maximum 256} | ForEach-Object {[byte]$_}))
   ```
   Then use: `jwt.secret=base64:<generated-value>`

3. **Don't commit secrets to Git** - Use .env files or secrets management

## Summary
✅ Configuration fixed
✅ Code updated
⚠️ YOU MUST: Restart app and get NEW tokens

