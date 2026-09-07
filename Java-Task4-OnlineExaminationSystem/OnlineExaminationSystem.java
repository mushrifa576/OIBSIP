import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OnlineExaminationSystem
 * ------------------------
 * A Java Swing GUI examination system: students log in, optionally update
 * their profile, then answer timed multiple-choice questions with
 * Next/Previous navigation and a live countdown that auto-submits at zero.
 *
 * Screens (managed with CardLayout):
 *   LOGIN -> PROFILE -> EXAM -> RESULT -> (Logout) -> LOGIN
 *
 * Run with:
 *   javac OnlineExaminationSystem.java
 *   java OnlineExaminationSystem
 */
public class OnlineExaminationSystem extends JFrame {

    // ================= Theme: "exam paper" palette =================
    static final Color BG          = new Color(0xF3, 0xF1, 0xEA); // cream paper
    static final Color CARD        = new Color(0xFF, 0xFF, 0xFF);
    static final Color INK         = new Color(0x22, 0x25, 0x2B);
    static final Color MUTED       = new Color(0x6B, 0x72, 0x80);
    static final Color ACCENT      = new Color(0x2D, 0x5F, 0xDB);
    static final Color ACCENT_DARK = new Color(0x1F, 0x3F, 0x99);
    static final Color SUCCESS     = new Color(0x1F, 0x9D, 0x55);
    static final Color DANGER      = new Color(0xD9, 0x36, 0x36);
    static final Color WARN        = new Color(0xE2, 0xA7, 0x3B);
    static final Color HAIRLINE    = new Color(0xE3, 0xE0, 0xD6);

    static final Font FONT_BRAND    = new Font("Segoe UI", Font.BOLD, 24);
    static final Font FONT_H2       = new Font("Segoe UI", Font.BOLD, 15);
    static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 14);
    static final Font FONT_BODY_B   = new Font("Segoe UI", Font.BOLD, 14);
    static final Font FONT_QUESTION = new Font("Georgia", Font.BOLD, 18);
    static final Font FONT_OPTION   = new Font("Georgia", Font.PLAIN, 15);
    static final Font FONT_TIMER    = new Font("Consolas", Font.BOLD, 22);
    static final Font FONT_HINT     = new Font("Segoe UI", Font.ITALIC, 11);

    // ================= Exam configuration =================
    static final int EXAM_DURATION_SECONDS = 1 * 60; // 30 minutes

    // ================= Demo user store =================
    static class UserRecord {
        String username, password, displayName;
        UserRecord(String username, String password, String displayName) {
            this.username = username;
            this.password = password;
            this.displayName = displayName;
        }
    }

    private final Map<String, UserRecord> users = new HashMap<>();

    {
        users.put("student1", new UserRecord("student1", "pass123", "Alex"));
        users.put("student2", new UserRecord("student2", "pass456", "Priya"));
    }

    // ================= Question bank =================
    static class Question {
        final String text;
        final String[] options;
        final int correctIndex;
        Question(String text, String[] options, int correctIndex) {
            this.text = text;
            this.options = options;
            this.correctIndex = correctIndex;
        }
    }

    private final Question[] questions = {
            new Question("Which of these is used to create an object in Java?",
                    new String[]{"new", "class", "static", "void"}, 0),
            new Question("Which keyword is used to inherit a class in Java?",
                    new String[]{"implements", "extends", "inherits", "super"}, 1),
            new Question("What is the default value of a boolean variable in Java?",
                    new String[]{"true", "false", "0", "null"}, 1),
            new Question("Which collection class allows duplicate elements and maintains insertion order?",
                    new String[]{"HashSet", "TreeSet", "ArrayList", "HashMap"}, 2),
            new Question("Which of these is not a Java primitive type?",
                    new String[]{"int", "float", "String", "char"}, 2),
            new Question("What does JVM stand for?",
                    new String[]{"Java Virtual Machine", "Java Verified Method",
                            "Java Variable Manager", "Java Visual Machine"}, 0),
            new Question("Which method is the entry point of a Java application?",
                    new String[]{"start()", "main()", "run()", "init()"}, 1),
            new Question("Which keyword is used to prevent a class from being inherited?",
                    new String[]{"static", "final", "private", "abstract"}, 1),
            new Question("Which operator is used to compare two values for equality in Java?",
                    new String[]{"=", "==", "equals", "<>"}, 1),
            new Question("Which of these is a checked exception in Java?",
                    new String[]{"NullPointerException", "ArrayIndexOutOfBoundsException",
                            "IOException", "ArithmeticException"}, 2),
    };

    // ================= State =================
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private UserRecord currentUser;
    private int[] userAnswers;          // -1 = unanswered, else selected option index
    private int currentQuestionIndex;
    private int remainingSeconds;
    private boolean examInProgress = false;
    private Timer examTimer;

    // ---- Login screen components ----
    private JTextField loginUserField;
    private JPasswordField loginPassField;
    private JLabel loginErrorLabel;

    // ---- Profile screen components ----
    private JTextField displayNameField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel profileErrorLabel;

    // ---- Exam screen components ----
    private JLabel timerLabel;
    private JLabel questionCounterLabel;
    private JLabel questionTextLabel;
    private JRadioButton[] optionButtons;
    private ButtonGroup optionGroup;
    private RoundedButton prevButton;
    private RoundedButton nextButton;
    private JPanel questionDotsPanel;
    private JButton[] questionDotButtons;

    // ---- Result screen components ----
    private JLabel scoreLabel;
    private JLabel timeTakenLabel;
    private JPanel breakdownPanel;
    private JLabel welcomeResultLabel;

    public OnlineExaminationSystem() {
        super("Online Examination System");
        getContentPane().setBackground(BG);
        setSize(680, 760);
        setMinimumSize(new Dimension(600, 640));
        setLocationRelativeTo(null);

        // We handle the close button ourselves so we can intercept it
        // during an active exam.
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClose();
            }
        });

        cards.setOpaque(false);
        cards.add(buildLoginPanel(), "LOGIN");
        cards.add(buildProfilePanel(), "PROFILE");
        cards.add(buildExamPanel(), "EXAM");
        cards.add(buildResultPanel(), "RESULT");
        setContentPane(cards);
        cardLayout.show(cards, "LOGIN");
    }

    private void handleWindowClose() {
        if (examInProgress) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to quit? Your exam progress will be lost.",
                    "Quit Exam?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
            // else: do nothing, keep the window open
        } else {
            System.exit(0);
        }
    }

    // =====================================================================
    // Login screen
    // =====================================================================

    private JPanel buildLoginPanel() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(BG);

        RoundedPanel card = new RoundedPanel(16, CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 34, 30, 34));
        card.setPreferredSize(new Dimension(360, 380));

        JLabel brand = new JLabel("\uD83D\uDCDD Online Examination");
        brand.setFont(FONT_BRAND);
        brand.setForeground(ACCENT_DARK);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Log in to begin your test");
        tagline.setFont(FONT_BODY);
        tagline.setForeground(MUTED);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        tagline.setBorder(new EmptyBorder(4, 0, 26, 0));

        loginUserField = styledTextField();
        JLabel passLbl = fieldLabel("Password");
        loginPassField = new JPasswordField();
        styleField(loginPassField);

        loginErrorLabel = new JLabel(" ");
        loginErrorLabel.setFont(FONT_HINT);
        loginErrorLabel.setForeground(DANGER);
        loginErrorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginErrorLabel.setBorder(new EmptyBorder(8, 0, 8, 0));

        RoundedButton loginBtn = new RoundedButton("Login", ACCENT, Color.WHITE);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.addActionListener(e -> attemptLogin());
        loginPassField.addActionListener(e -> attemptLogin());

        JLabel demoHint = new JLabel("<html><center>Demo logins:<br>student1 / pass123<br>student2 / pass456</center></html>");
        demoHint.setFont(FONT_HINT);
        demoHint.setForeground(MUTED);
        demoHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        demoHint.setBorder(new EmptyBorder(18, 0, 0, 0));

        card.add(brand);
        card.add(tagline);
        card.add(fieldLabel("Username"));
        card.add(Box.createVerticalStrut(4));
        card.add(loginUserField);
        card.add(Box.createVerticalStrut(14));
        card.add(passLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(loginPassField);
        card.add(loginErrorLabel);
        card.add(loginBtn);
        card.add(demoHint);

        wrap.add(card);
        return wrap;
    }

    private void attemptLogin() {
        String username = loginUserField.getText().trim();
        String password = new String(loginPassField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            loginErrorLabel.setText("Please enter both username and password.");
            return;
        }

        UserRecord user = users.get(username);
        if (user == null || !user.password.equals(password)) {
            loginErrorLabel.setText("Invalid username or password.");
            loginPassField.setText("");
            return;
        }

        currentUser = user;
        loginErrorLabel.setText(" ");
        loginPassField.setText("");
        loginUserField.setText("");

        // Pre-fill the profile screen with the current display name.
        displayNameField.setText(currentUser.displayName);
        newPasswordField.setText("");
        confirmPasswordField.setText("");
        profileErrorLabel.setText(" ");

        cardLayout.show(cards, "PROFILE");
    }

    // =====================================================================
    // Profile update screen
    // =====================================================================

    private JPanel buildProfilePanel() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(BG);

        RoundedPanel card = new RoundedPanel(16, CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 34, 30, 34));
        card.setPreferredSize(new Dimension(380, 460));

        JLabel title = new JLabel("Update Your Profile");
        title.setFont(FONT_BRAND.deriveFont(20f));
        title.setForeground(ACCENT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Optional \u2014 change your display name or password before starting.");
        subtitle.setFont(FONT_BODY);
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setBorder(new EmptyBorder(4, 0, 22, 0));

        displayNameField = styledTextField();
        newPasswordField = new JPasswordField();
        styleField(newPasswordField);
        confirmPasswordField = new JPasswordField();
        styleField(confirmPasswordField);

        profileErrorLabel = new JLabel(" ");
        profileErrorLabel.setFont(FONT_HINT);
        profileErrorLabel.setForeground(DANGER);
        profileErrorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        profileErrorLabel.setBorder(new EmptyBorder(8, 0, 8, 0));

        RoundedButton continueBtn = new RoundedButton("Continue to Exam", ACCENT, Color.WHITE);
        continueBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        continueBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        continueBtn.addActionListener(e -> attemptContinueToExam());

        JLabel hint = new JLabel("Leave password fields blank to keep your current password.");
        hint.setFont(FONT_HINT);
        hint.setForeground(MUTED);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        hint.setBorder(new EmptyBorder(14, 0, 0, 0));

        card.add(title);
        card.add(subtitle);
        card.add(fieldLabel("Display Name"));
        card.add(Box.createVerticalStrut(4));
        card.add(displayNameField);
        card.add(Box.createVerticalStrut(14));
        card.add(fieldLabel("New Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(newPasswordField);
        card.add(Box.createVerticalStrut(14));
        card.add(fieldLabel("Confirm New Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(confirmPasswordField);
        card.add(profileErrorLabel);
        card.add(continueBtn);
        card.add(hint);

        wrap.add(card);
        return wrap;
    }

    private void attemptContinueToExam() {
        String newName = displayNameField.getText().trim();
        String newPass = new String(newPasswordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        if (newName.isEmpty()) {
            profileErrorLabel.setText("Display name cannot be empty.");
            return;
        }

        boolean wantsPasswordChange = !newPass.isEmpty() || !confirmPass.isEmpty();
        if (wantsPasswordChange) {
            if (newPass.length() < 4) {
                profileErrorLabel.setText("New password must be at least 4 characters.");
                return;
            }
            if (!newPass.equals(confirmPass)) {
                profileErrorLabel.setText("Passwords do not match.");
                return;
            }
        }

        currentUser.displayName = newName;
        if (wantsPasswordChange) {
            currentUser.password = newPass;
        }
        profileErrorLabel.setText(" ");

        startExam();
    }

    // =====================================================================
    // Exam screen
    // =====================================================================

    private JPanel buildExamPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(20, 26, 20, 26));

        // ---- Top bar: counter + timer ----
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        questionCounterLabel = new JLabel("Question 1 of " + questions.length);
        questionCounterLabel.setFont(FONT_H2);
        questionCounterLabel.setForeground(INK);
        topBar.add(questionCounterLabel, BorderLayout.WEST);

        JPanel timerWrap = new JPanel();
        timerWrap.setOpaque(false);
        timerLabel = new JLabel("30:00");
        timerLabel.setFont(FONT_TIMER);
        timerLabel.setForeground(ACCENT_DARK);
        timerWrap.add(timerLabel);
        topBar.add(timerWrap, BorderLayout.EAST);

        root.add(topBar, BorderLayout.NORTH);

        // ---- Center: question card ----
        RoundedPanel questionCard = new RoundedPanel(14, CARD);
        questionCard.setLayout(new BoxLayout(questionCard, BoxLayout.Y_AXIS));
        questionCard.setBorder(new EmptyBorder(24, 26, 24, 26));

        questionTextLabel = new JLabel();
        questionTextLabel.setFont(FONT_QUESTION);
        questionTextLabel.setForeground(INK);
        questionTextLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionCard.add(questionTextLabel);
        questionCard.add(Box.createVerticalStrut(20));

        optionGroup = new ButtonGroup();
        optionButtons = new JRadioButton[4];
        for (int i = 0; i < 4; i++) {
            JRadioButton rb = new JRadioButton();
            rb.setFont(FONT_OPTION);
            rb.setForeground(INK);
            rb.setOpaque(false);
            rb.setAlignmentX(Component.LEFT_ALIGNMENT);
            rb.setFocusPainted(false);
            rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            rb.setBorder(new EmptyBorder(8, 4, 8, 4));
            optionButtons[i] = rb;
            optionGroup.add(rb);
            questionCard.add(rb);
        }

        JScrollPane questionScroll = new JScrollPane(questionCard);
        questionScroll.setBorder(BorderFactory.createEmptyBorder());
        questionScroll.getViewport().setBackground(BG);
        questionScroll.setBackground(BG);
        questionScroll.getVerticalScrollBar().setUnitIncrement(14);
        root.add(questionScroll, BorderLayout.CENTER);

        // ---- Bottom: nav dots + Prev/Next/Submit ----
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        questionDotsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        questionDotsPanel.setOpaque(false);
        questionDotButtons = new JButton[questions.length];
        for (int i = 0; i < questions.length; i++) {
            final int idx = i;
            JButton dot = new JButton(String.valueOf(i + 1));
            dot.setFont(FONT_HINT.deriveFont(Font.PLAIN, 11f));
            dot.setMargin(new Insets(2, 2, 2, 2));
            dot.setPreferredSize(new Dimension(26, 26));
            dot.setFocusPainted(false);
            dot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            dot.addActionListener(e -> jumpToQuestion(idx));
            questionDotButtons[i] = dot;
            questionDotsPanel.add(dot);
        }
        bottom.add(questionDotsPanel);
        bottom.add(Box.createVerticalStrut(10));

        JPanel navRow = new JPanel(new BorderLayout());
        navRow.setOpaque(false);

        prevButton = new RoundedButton("\u2190 Previous", new Color(0xE7, 0xE4, 0xDA), INK);
        prevButton.setPreferredSize(new Dimension(120, 40));
        prevButton.addActionListener(e -> goToPrevious());

        nextButton = new RoundedButton("Next \u2192", ACCENT, Color.WHITE);
        nextButton.setPreferredSize(new Dimension(120, 40));
        nextButton.addActionListener(e -> goToNext());

        RoundedButton submitButton = new RoundedButton("Submit Exam", DANGER, Color.WHITE);
        submitButton.setPreferredSize(new Dimension(140, 40));
        submitButton.addActionListener(e -> attemptManualSubmit());

        JPanel leftNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftNav.setOpaque(false);
        leftNav.add(prevButton);

        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightNav.setOpaque(false);
        rightNav.add(submitButton);
        rightNav.add(nextButton);

        navRow.add(leftNav, BorderLayout.WEST);
        navRow.add(rightNav, BorderLayout.EAST);
        bottom.add(navRow);

        root.add(bottom, BorderLayout.SOUTH);
        return root;
    }

    private void startExam() {
        userAnswers = new int[questions.length];
        for (int i = 0; i < userAnswers.length; i++) userAnswers[i] = -1;
        currentQuestionIndex = 0;
        remainingSeconds = EXAM_DURATION_SECONDS;
        examInProgress = true;

        showQuestion(0);
        updateTimerLabel();

        if (examTimer != null) examTimer.stop();
        examTimer = new Timer(1000, e -> onTimerTick());
        examTimer.start();

        cardLayout.show(cards, "EXAM");
    }

    private void onTimerTick() {
        remainingSeconds--;
        updateTimerLabel();
        if (remainingSeconds <= 0) {
            examTimer.stop();
            finalizeExam(true);
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                    this,
                    "Time's up! Your exam has been submitted automatically.",
                    "Time's Up",
                    JOptionPane.INFORMATION_MESSAGE
            ));
        }
    }

    private void updateTimerLabel() {
        timerLabel.setText(formatTime(Math.max(remainingSeconds, 0)));
        timerLabel.setForeground(remainingSeconds <= 60 ? DANGER : (remainingSeconds <= 300 ? WARN : ACCENT_DARK));
    }

    static String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    private void showQuestion(int index) {
        currentQuestionIndex = index;
        Question q = questions[index];
        questionCounterLabel.setText("Question " + (index + 1) + " of " + questions.length);
        questionTextLabel.setText("<html><body style='width:420px'>" + (index + 1) + ". " + escapeHtml(q.text) + "</body></html>");

        optionGroup.clearSelection();
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(charFor(i) + ")  " + q.options[i]);
        }
        if (userAnswers[index] != -1) {
            optionButtons[userAnswers[index]].setSelected(true);
        }

        prevButton.setEnabled(index > 0);
        nextButton.setEnabled(index < questions.length - 1);

        refreshQuestionDots();
    }

    private static String charFor(int i) {
        return String.valueOf((char) ('A' + i));
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void refreshQuestionDots() {
        for (int i = 0; i < questionDotButtons.length; i++) {
            JButton dot = questionDotButtons[i];
            boolean answered = userAnswers[i] != -1;
            boolean current = i == currentQuestionIndex;
            if (current) {
                dot.setBackground(ACCENT_DARK);
                dot.setForeground(Color.WHITE);
            } else if (answered) {
                dot.setBackground(new Color(0xD7, 0xE3, 0xFB));
                dot.setForeground(ACCENT_DARK);
            } else {
                dot.setBackground(new Color(0xEC, 0xE9, 0xDF));
                dot.setForeground(MUTED);
            }
            dot.setOpaque(true);
            dot.setBorderPainted(false);
        }
    }

    /** Reads the currently selected radio button (if any) into userAnswers[]. */
    private void saveCurrentSelection() {
        int selected = -1;
        for (int i = 0; i < optionButtons.length; i++) {
            if (optionButtons[i].isSelected()) {
                selected = i;
                break;
            }
        }
        userAnswers[currentQuestionIndex] = selected;
    }

    private void goToPrevious() {
        saveCurrentSelection();
        if (currentQuestionIndex > 0) {
            showQuestion(currentQuestionIndex - 1);
        }
    }

    private void goToNext() {
        saveCurrentSelection();
        if (currentQuestionIndex < questions.length - 1) {
            showQuestion(currentQuestionIndex + 1);
        }
    }

    private void jumpToQuestion(int index) {
        if (index == currentQuestionIndex) return;
        saveCurrentSelection();
        showQuestion(index);
    }

    private void attemptManualSubmit() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to submit the exam? You won't be able to change your answers after this.",
                "Submit Exam?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            if (examTimer != null) examTimer.stop();
            finalizeExam(false);
        }
    }

    // =====================================================================
    // Result screen
    // =====================================================================

    private JPanel buildResultPanel() {
        JPanel outer = new JPanel();
        outer.setOpaque(false);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(new EmptyBorder(24, 26, 24, 26));
        outer.setBackground(BG);

        RoundedPanel summaryCard = new RoundedPanel(14, CARD);
        summaryCard.setLayout(new BoxLayout(summaryCard, BoxLayout.Y_AXIS));
        summaryCard.setBorder(new EmptyBorder(24, 26, 24, 26));
        summaryCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));

        welcomeResultLabel = new JLabel("Exam Complete");
        welcomeResultLabel.setFont(FONT_BRAND.deriveFont(20f));
        welcomeResultLabel.setForeground(ACCENT_DARK);
        welcomeResultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        scoreLabel = new JLabel("Score: -- out of --");
        scoreLabel.setFont(FONT_H2.deriveFont(18f));
        scoreLabel.setForeground(INK);
        scoreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        scoreLabel.setBorder(new EmptyBorder(12, 0, 4, 0));

        timeTakenLabel = new JLabel("Time Taken: --:--");
        timeTakenLabel.setFont(FONT_BODY);
        timeTakenLabel.setForeground(MUTED);
        timeTakenLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        summaryCard.add(welcomeResultLabel);
        summaryCard.add(scoreLabel);
        summaryCard.add(timeTakenLabel);
        outer.add(summaryCard);
        outer.add(Box.createVerticalStrut(14));

        JLabel breakdownTitle = new JLabel("Answer Breakdown");
        breakdownTitle.setFont(FONT_H2);
        breakdownTitle.setForeground(INK);
        breakdownTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        breakdownTitle.setBorder(new EmptyBorder(4, 4, 8, 0));
        outer.add(breakdownTitle);

        breakdownPanel = new JPanel();
        breakdownPanel.setLayout(new BoxLayout(breakdownPanel, BoxLayout.Y_AXIS));
        breakdownPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(breakdownPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG);
        scroll.setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        outer.add(scroll);

        outer.add(Box.createVerticalStrut(14));
        RoundedButton logoutBtn = new RoundedButton("Logout", ACCENT, Color.WHITE);
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(160, 42));
        logoutBtn.addActionListener(e -> logout());
        outer.add(logoutBtn);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG);
        wrap.add(outer, BorderLayout.CENTER);
        return wrap;
    }

    private void finalizeExam(boolean autoSubmitted) {
        saveCurrentSelection();
        examInProgress = false;
        if (examTimer != null) examTimer.stop();

        int score = 0;
        for (int i = 0; i < questions.length; i++) {
            if (userAnswers[i] == questions[i].correctIndex) score++;
        }
        int timeTakenSeconds = Math.max(0, EXAM_DURATION_SECONDS - Math.max(remainingSeconds, 0));

        welcomeResultLabel.setText(autoSubmitted ? "Time's Up \u2014 Exam Auto-Submitted" : "Exam Complete");
        scoreLabel.setText("Score: " + score + " out of " + questions.length);
        timeTakenLabel.setText("Time Taken: " + formatTime(timeTakenSeconds)
                + "   \u00B7   Candidate: " + currentUser.displayName);

        breakdownPanel.removeAll();
        for (int i = 0; i < questions.length; i++) {
            breakdownPanel.add(buildBreakdownRow(i));
            breakdownPanel.add(Box.createVerticalStrut(8));
        }
        breakdownPanel.revalidate();
        breakdownPanel.repaint();

        cardLayout.show(cards, "RESULT");
    }

    private JPanel buildBreakdownRow(int index) {
        Question q = questions[index];
        int userAns = userAnswers[index];
        boolean correct = userAns == q.correctIndex;
        boolean answered = userAns != -1;

        RoundedPanel row = new RoundedPanel(10, CARD);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setBorder(new EmptyBorder(12, 14, 12, 14));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        String icon = !answered ? "\u26AA" : (correct ? "\u2705" : "\u274C");
        Color statusColor = !answered ? MUTED : (correct ? SUCCESS : DANGER);

        JLabel qLabel = new JLabel("<html><body style='width:460px'>" + icon + " Q" + (index + 1) + ". "
                + escapeHtml(q.text) + "</body></html>");
        qLabel.setFont(FONT_BODY_B);
        qLabel.setForeground(INK);
        qLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(qLabel);

        JLabel yourAnswer = new JLabel("Your answer: " + (answered ? charFor(userAns) + ") " + q.options[userAns] : "Not answered"));
        yourAnswer.setFont(FONT_BODY);
        yourAnswer.setForeground(statusColor);
        yourAnswer.setAlignmentX(Component.LEFT_ALIGNMENT);
        yourAnswer.setBorder(new EmptyBorder(6, 18, 0, 0));
        row.add(yourAnswer);

        if (!correct) {
            JLabel correctAnswer = new JLabel("Correct answer: " + charFor(q.correctIndex) + ") " + q.options[q.correctIndex]);
            correctAnswer.setFont(FONT_BODY);
            correctAnswer.setForeground(SUCCESS);
            correctAnswer.setAlignmentX(Component.LEFT_ALIGNMENT);
            correctAnswer.setBorder(new EmptyBorder(2, 18, 0, 0));
            row.add(correctAnswer);
        }

        return row;
    }

    private void logout() {
        currentUser = null;
        userAnswers = null;
        examInProgress = false;
        if (examTimer != null) examTimer.stop();
        cardLayout.show(cards, "LOGIN");
    }

    // =====================================================================
    // Form-building helpers
    // =====================================================================

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY_B);
        l.setForeground(MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField styledTextField() {
        JTextField f = new JTextField();
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setFont(FONT_BODY);
        f.setForeground(INK);
        f.setCaretColor(INK);
        f.setBackground(new Color(0xFA, 0xF9, 0xF5));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HAIRLINE, 1),
                new EmptyBorder(8, 10, 8, 10)));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    // =====================================================================
    // Custom components
    // =====================================================================

    static class RoundedPanel extends JPanel {
        private final int radius;
        RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            setOpaque(false);
            setBackground(bg);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class RoundedButton extends JButton {
        private final Color base;
        private boolean hover = false;

        RoundedButton(String text, Color base, Color fg) {
            super(text);
            this.base = base;
            setForeground(fg);
            setFont(FONT_BODY_B);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 16, 8, 16));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color bg = isEnabled() ? (hover ? base.brighter() : base) : new Color(0xD8, 0xD5, 0xCA);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =====================================================================
    // Entry point
    // =====================================================================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new OnlineExaminationSystem().setVisible(true);
        });
    }
}