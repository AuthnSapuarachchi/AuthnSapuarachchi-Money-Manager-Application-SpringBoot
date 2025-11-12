# Email Implementation Guide - Income Report with Excel Attachment

## ✅ Implementation Status

All backend files have been successfully created and configured!

## 📁 Files Created/Updated

### 1. **EmailService.java** ✅
- Location: `src/main/java/com/authcodelab/smartmoneymanageapp/service/EmailService.java`
- Features:
  - `sendEmail()` - Simple email sending
  - `sendIncomeReport()` - Sends income report with Excel attachment
  - Uses Apache POI to generate Excel files
  - Generates HTML email body with summary

### 2. **IncomeEmailDTO.java** ✅
- Location: `src/main/java/com/authcodelab/smartmoneymanageapp/dto/IncomeEmailDTO.java`
- Fields:
  - `date` - LocalDate
  - `source` - String
  - `amount` - BigDecimal
  - `description` - String

### 3. **SendIncomeEmailRequest.java** ✅
- Location: `src/main/java/com/authcodelab/smartmoneymanageapp/dto/SendIncomeEmailRequest.java`
- Fields:
  - `recipient` - Email address
  - `incomes` - List of IncomeEmailDTO

### 4. **EmailController.java** ✅
- Location: `src/main/java/com/authcodelab/smartmoneymanageapp/controller/EmailController.java`
- Endpoint: `POST /api/v1.0/email/income`
- Accepts: `SendIncomeEmailRequest` JSON
- Returns: Success/Error message

## 🔧 Configuration Already Present

### pom.xml Dependencies ✅
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

### application.properties Configuration ✅
```properties
# Email Configuration
spring.mail.host=${BREVO_HOST}
spring.mail.port=${BREVO_PORT}
spring.mail.username=${BREVO_USERNAME}
spring.mail.password=${BREVO_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.protocols=smtp
spring.mail.properties.mail.smtp.from=${BREVO_FROM_EMAIL}
```

## 🚀 Next Steps

### 1. Set Environment Variables

You need to set these environment variables for email to work:

**For Gmail:**
```bash
BREVO_HOST=smtp.gmail.com
BREVO_PORT=587
BREVO_USERNAME=your.email@gmail.com
BREVO_PASSWORD=your-app-password
BREVO_FROM_EMAIL=your.email@gmail.com
```

**For Gmail - Get App Password:**
1. Go to Google Account → Security
2. Enable 2-Step Verification
3. Go to "App passwords" (at the bottom)
4. Generate a new app password for "Mail"
5. Use that 16-character password as `BREVO_PASSWORD`

**For Brevo (SendinBlue):**
```bash
BREVO_HOST=smtp-relay.brevo.com
BREVO_PORT=587
BREVO_USERNAME=your-brevo-login
BREVO_PASSWORD=your-brevo-smtp-key
BREVO_FROM_EMAIL=your-verified-email@domain.com
```

### 2. Build and Test

```bash
# Clean and rebuild
mvn clean install

# Or if using Maven wrapper
./mvnw clean install

# Run the application
mvn spring-boot:run
```

### 3. Test the Endpoint

**Using cURL:**
```bash
curl -X POST http://localhost:8081/api/v1.0/email/income \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "recipient": "test@example.com",
    "incomes": [
      {
        "date": "2025-11-01",
        "source": "Salary",
        "amount": 5000.00,
        "description": "Monthly salary"
      },
      {
        "date": "2025-11-10",
        "source": "Freelance",
        "amount": 1500.00,
        "description": "Website project"
      }
    ]
  }'
```

**Expected Response:**
```json
"Income report sent successfully to test@example.com"
```

## 📧 What the Email Contains

### Subject
`Income Report`

### Body (HTML)
```html
<h2>Income Report</h2>
<p>Please find attached the complete income report.</p>
<p><strong>Total Income: </strong>6500.00</p>
<p><strong>Total Records: </strong>2</p>
```

### Attachment
`income-report.xlsx` - Excel file with columns:
- Date
- Source
- Amount
- Description

## 🎨 Frontend Integration (Next)

Once backend is working, update your frontend Income component:

```javascript
const handleEmailReport = async () => {
  const email = prompt("Enter email address to send the income report:");
  
  if (!email) return;
  
  // Validate email
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    toast.error("Invalid email address");
    return;
  }

  try {
    const response = await fetch("http://localhost:8081/api/v1.0/email/income", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${yourJwtToken}`
      },
      body: JSON.stringify({
        recipient: email,
        incomes: incomeData.map(income => ({
          date: income.date,
          source: income.name,
          amount: income.amount,
          description: income.description || ""
        }))
      })
    });

    if (response.ok) {
      toast.success(`Income report sent successfully to ${email}`);
    } else {
      toast.error("Failed to send email");
    }
  } catch (error) {
    toast.error("Error sending email: " + error.message);
  }
};
```

## 🐛 Troubleshooting

### Issue: Email not sending
**Solution:** Check environment variables and SMTP credentials

### Issue: "Authentication failed"
**Solution:** For Gmail, make sure you're using an App Password, not your regular password

### Issue: "Connection timeout"
**Solution:** Check firewall settings and ensure port 587 is open

### Issue: Excel file is empty
**Solution:** Verify that income data is being passed correctly in the request

## 📝 Testing Checklist

- [ ] Environment variables are set correctly
- [ ] Application starts without errors
- [ ] Can authenticate and get JWT token
- [ ] Can call the email endpoint with valid data
- [ ] Email is received with Excel attachment
- [ ] Excel file opens correctly with data
- [ ] HTML email body displays correctly

## 🎉 Success Indicators

When everything is working, you should:
1. ✅ Receive an email at the specified address
2. ✅ Email has a professional HTML body with summary
3. ✅ Excel attachment is included
4. ✅ Excel file contains all income records with proper formatting
5. ✅ Total amount is calculated correctly

## 📞 Support

If you encounter any issues:
1. Check application logs for error messages
2. Verify all environment variables are set
3. Test SMTP connection separately
4. Ensure JWT authentication is working

---

**Implementation Date:** November 13, 2025
**Status:** ✅ Backend Complete - Ready for Testing

