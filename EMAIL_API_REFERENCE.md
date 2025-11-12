# 📧 Email API Quick Reference

## Endpoint Details

**URL:** `POST /api/v1.0/email/income`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN
```

**Request Body:**
```json
{
  "recipient": "recipient@example.com",
  "incomes": [
    {
      "date": "2025-11-13",
      "source": "Salary",
      "amount": 5000.00,
      "description": "Monthly salary payment"
    },
    {
      "date": "2025-11-10",
      "source": "Freelance Project",
      "amount": 1500.50,
      "description": "Website development"
    }
  ]
}
```

**Success Response (200):**
```json
"Income report sent successfully to recipient@example.com"
```

**Error Response (500):**
```json
"Failed to send email: [error details]"
```

## 📊 Excel File Structure

The generated Excel file (`income-report.xlsx`) contains:

| Date       | Source            | Amount  | Description              |
|------------|-------------------|---------|--------------------------|
| 2025-11-13 | Salary            | 5000.00 | Monthly salary payment   |
| 2025-11-10 | Freelance Project | 1500.50 | Website development      |

## 🎯 Test Command (cURL)

```bash
curl -X POST http://localhost:8081/api/v1.0/email/income \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "recipient": "your-email@example.com",
    "incomes": [
      {
        "date": "2025-11-13",
        "source": "Test Income",
        "amount": 1000.00,
        "description": "Test description"
      }
    ]
  }'
```

## 🔐 Security Notes

- ✅ Endpoint requires JWT authentication
- ✅ Email validation on backend
- ✅ SMTP credentials stored in environment variables
- ✅ No sensitive data exposed in responses

## 📱 Frontend Integration Example

```javascript
// React/JavaScript example
const sendIncomeReport = async (email, incomeData) => {
  const token = localStorage.getItem('jwtToken'); // or your auth method
  
  const response = await fetch('http://localhost:8081/api/v1.0/email/income', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      recipient: email,
      incomes: incomeData.map(income => ({
        date: income.date,
        source: income.name || income.source,
        amount: income.amount,
        description: income.description || ''
      }))
    })
  });
  
  if (response.ok) {
    const message = await response.text();
    console.log(message);
    return true;
  } else {
    const error = await response.text();
    console.error(error);
    return false;
  }
};

// Usage
const email = prompt('Enter recipient email:');
if (email) {
  const success = await sendIncomeReport(email, myIncomeData);
  if (success) {
    alert('Email sent successfully!');
  } else {
    alert('Failed to send email');
  }
}
```

## ⚙️ Environment Variables Required

```bash
# Gmail Configuration
BREVO_HOST=smtp.gmail.com
BREVO_PORT=587
BREVO_USERNAME=your-email@gmail.com
BREVO_PASSWORD=your-16-char-app-password
BREVO_FROM_EMAIL=your-email@gmail.com

# Or Brevo (SendinBlue) Configuration
BREVO_HOST=smtp-relay.brevo.com
BREVO_PORT=587
BREVO_USERNAME=your-brevo-username
BREVO_PASSWORD=your-brevo-smtp-key
BREVO_FROM_EMAIL=your-verified-email@domain.com
```

## 🧪 Postman Collection

Import this JSON to test in Postman:

```json
{
  "info": {
    "name": "Income Email API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Send Income Report",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          },
          {
            "key": "Authorization",
            "value": "Bearer {{jwt_token}}"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"recipient\": \"test@example.com\",\n  \"incomes\": [\n    {\n      \"date\": \"2025-11-13\",\n      \"source\": \"Salary\",\n      \"amount\": 5000.00,\n      \"description\": \"Monthly salary\"\n    }\n  ]\n}"
        },
        "url": {
          "raw": "http://localhost:8081/api/v1.0/email/income",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["api", "v1.0", "email", "income"]
        }
      }
    }
  ]
}
```

---

**Quick Start:** Set environment variables → Build project → Test endpoint → Integrate frontend

