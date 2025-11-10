package com.authcodelab.smartmoneymanageapp.service;

import com.authcodelab.smartmoneymanageapp.dto.ExpenseDTO;
import com.authcodelab.smartmoneymanageapp.entity.ProfileEntity;
import com.authcodelab.smartmoneymanageapp.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 22 * * *", zone = "IST") // Every day at 10pm
    public void sendDailyIncomeExpenseReminder() {
        log.info("Job started: Sending daily income/expense reminder emails to users.");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for(ProfileEntity profile : profiles) {
            String body = "Hi" + profile.getFullName() + ",\n\n" +
                    "This is a friendly reminder to log your daily income and expenses. Keeping track of your finances is crucial for effective money management.\n\n" +
                    "You can log your transactions by visiting the following link:\n" +
                    frontendUrl + "/dashboard\n\n" +
                    "Thank you for using Smart Money Manage App!\n\n" +
                    "Best regards,\n" +
                    "Smart Money Manage App Team";
            emailService.sendEmail(profile.getEmail(), "Daily Income/Expense Reminder", body);
        }
    }


    @Scheduled(cron = "0 0 23 * * *", zone = "IST")
    public void sendDailyExpenseSummary() {
        log.info("Job started: Sending daily expense summary emails to users.");
        List<ProfileEntity> profiles = profileRepository.findAll();

        for (ProfileEntity profile : profiles) {
            List<ExpenseDTO> todaysExpense = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());
            if (todaysExpense == null || todaysExpense.isEmpty()) {
                continue;
            }

            StringBuilder table = new StringBuilder();
            table.append("<table style='border-collapse:collapse;width:100%;font-family:Arial,sans-serif;'>");
            table.append("<thead>");
            table.append("<tr style='background-color:#4CAF50;color:white;'>");
            table.append("<th style='border:1px solid #ddd;padding:12px;text-align:left;'>No</th>");
            table.append("<th style='border:1px solid #ddd;padding:12px;text-align:left;'>Name</th>");
            table.append("<th style='border:1px solid #ddd;padding:12px;text-align:right;'>Amount</th>");
            table.append("<th style='border:1px solid #ddd;padding:12px;text-align:left;'>Category</th>");
            table.append("<th style='border:1px solid #ddd;padding:12px;text-align:left;'>Date</th>");
            table.append("</tr>");
            table.append("</thead>");
            table.append("<tbody>");

            int i = 1;
            for (ExpenseDTO expense : todaysExpense) {
                String name = expense.getName() != null ? escapeHtml(expense.getName()) : "";
                String amount = expense.getAmount() != null ? expense.getAmount().toString() : "0.00";
                String category = expense.getCategoryName() != null ? escapeHtml(expense.getCategoryName()) : "N/A";
                String date = expense.getDate() != null ? expense.getDate().toString() : "";

                table.append("<tr style='background-color:").append(i % 2 == 0 ? "#f2f2f2" : "white").append(";'>");
                table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(i++).append("</td>");
                table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(name).append("</td>");
                table.append("<td style='border:1px solid #ddd;padding:8px;text-align:right;'>").append(amount).append("</td>");
                table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(category).append("</td>");
                table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(date).append("</td>");
                table.append("</tr>");
            }

            table.append("</tbody>");
            table.append("</table>");

            String body = "Hi " + escapeHtml(profile.getFullName()) + ", <br/><br/>" +
                    "Here is a summary of your expenses for today:<br/><br/>" +
                    table +
                    "<br/><br/>Keep up the good work in managing your finances!<br/><br/>" +
                    "Best regards,<br/>Smart Money Manage App Team";

            emailService.sendEmail(profile.getEmail(), "Today's Expense Summary", body);
        }
        log.info("Job completed: Sending daily expense summary emails to users.");
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
