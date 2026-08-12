import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * NumberGuessingGame
 * ------------------
 * A Swing GUI number-guessing game with a modern, hand-styled dark UI
 * (rounded cards, a segmented difficulty picker, an attempt-dot tracker,
 * and a styled round-history list).
 *
 * Features:
 *  - Random number generated each round (range depends on difficulty)
 *  - Guess entered via a JTextField
 *  - "Too High!" / "Too Low!" / "Correct!" feedback
 *  - Visible attempt counter (dots + text)
 *  - Maximum attempts per difficulty; "You Lost!" + reveal on failure
 *  - "Play Again?" dialog after each round
 *  - Running score/history log: "Round X - guessed in Y attempts"
 *  - Difficulty levels: Easy (1-50, 10 attempts), Medium (1-100, 7 attempts),
 *    Hard (1-200, 5 attempts)
 */
public class NumberGuessingGame extends JFrame {

    // ================= Palette =================
    private static final Color BG          = new Color(0x14, 0x12, 0x1F);
    private static final Color CARD        = new Color(0x1E, 0x1B, 0x2E);
    private static final Color CARD_LIGHT  = new Color(0x27, 0x23, 0x3B);
    private static final Color ACCENT      = new Color(0x8B, 0x5C, 0xF6);
    private static final Color ACCENT_DIM  = new Color(0x5B, 0x46, 0x8E);
    private static final Color SUCCESS     = new Color(0x4A, 0xDE, 0x80);
    private static final Color WARN        = new Color(0xFB, 0x92, 0x3C);
    private static final Color DANGER      = new Color(0xF8, 0x71, 0x71);
    private static final Color INFO        = new Color(0x60, 0xA5, 0xFA);
    private static final Color TEXT        = new Color(0xF3, 0xF1, 0xFA);
    private static final Color TEXT_DIM    = new Color(0xA7, 0xA1, 0xC2);

    private static final Font FONT_TITLE   = new Font("SansSerif", Font.BOLD, 26);
    private static final Font FONT_SUB     = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONT_H2      = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_BODY    = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_BODY_B  = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_FEEDBACK= new Font("SansSerif", Font.BOLD, 24);
    private static final Font FONT_MONO    = new Font("Monospaced", Font.BOLD, 13);

    // ================= Difficulty =================
    private enum Difficulty {
        EASY("Easy", 1, 50, 10),
        MEDIUM("Medium", 1, 100, 7),
        HARD("Hard", 1, 200, 5);

        final String label;
        final int min, max, maxAttempts;

        Difficulty(String label, int min, int max, int maxAttempts) {
            this.label = label;
            this.min = min;
            this.max = max;
            this.maxAttempts = maxAttempts;
        }
    }

    // ================= Round history record =================
    private static final class RoundRecord {
        final int roundNumber;
        final Difficulty difficulty;
        final boolean won;
        final int secretNumber;
        final int attemptsUsed;
        final int maxAttempts;

        RoundRecord(int roundNumber, Difficulty difficulty, boolean won,
                    int secretNumber, int attemptsUsed, int maxAttempts) {
            this.roundNumber = roundNumber;
            this.difficulty = difficulty;
            this.won = won;
            this.secretNumber = secretNumber;
            this.attemptsUsed = attemptsUsed;
            this.maxAttempts = maxAttempts;
        }
    }

    // ================= Game state =================
    private final Random random = new Random();
    private int secretNumber;
    private int attemptsUsed;
    private int roundNumber = 0;
    private Difficulty currentDifficulty = Difficulty.MEDIUM;
    private boolean roundActive = false;

    // ================= UI components =================
    private final PillButton[] difficultyButtons = new PillButton[Difficulty.values().length];
    private JLabel rangeLabel;
    private JLabel attemptsText;
    private AttemptDots attemptDots;
    private RoundedPanel feedbackCard;
    private JLabel feedbackIcon;
    private JLabel feedbackLabel;
    private JTextField guessField;
    private PillButton guessButton;
    private PillButton newRoundButton;
    private JList<RoundRecord> historyList;
    private DefaultListModel<RoundRecord> historyModel;
    private JLabel statsLabel;

    private int wins = 0;

    public NumberGuessingGame() {
        super("Number Guessing Game");
        getContentPane().setBackground(BG);
        buildUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 760);
        setMinimumSize(new Dimension(460, 640));
        setLocationRelativeTo(null);
    }

    // =====================================================================
    // UI construction
    // =====================================================================

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(24, 24, 24, 24));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(buildDifficultyCard());
        center.add(Box.createVerticalStrut(14));
        center.add(buildPlayCard());
        center.add(Box.createVerticalStrut(14));
        center.add(buildHistoryCard());
        root.add(center, BorderLayout.CENTER);

        // All cards (and their components, e.g. attemptDots) now exist,
        // so it's safe to initialize the default difficulty selection.
        selectDifficulty(Difficulty.MEDIUM);
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("\uD83C\uDFAF  Number Guessing Game");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Pick a difficulty, then try to nail the secret number.");
        subtitle.setFont(FONT_SUB);
        subtitle.setForeground(TEXT_DIM);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setBorder(new EmptyBorder(4, 2, 0, 0));

        header.add(title);
        header.add(subtitle);
        return header;
    }

    private JComponent buildDifficultyCard() {
        RoundedPanel card = new RoundedPanel(18, CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JLabel h2 = sectionLabel("DIFFICULTY");
        card.add(h2);
        card.add(Box.createVerticalStrut(10));

        JPanel segmented = new JPanel(new GridLayout(1, 3, 8, 0));
        segmented.setOpaque(false);
        Difficulty[] all = Difficulty.values();
        for (int i = 0; i < all.length; i++) {
            Difficulty d = all[i];
            PillButton b = new PillButton(d.label, PillButton.Style.SEGMENT);
            b.addActionListener(e -> selectDifficulty(d));
            difficultyButtons[i] = b;
            segmented.add(b);
        }
        segmented.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(segmented);
        card.add(Box.createVerticalStrut(10));

        rangeLabel = new JLabel();
        rangeLabel.setFont(FONT_BODY);
        rangeLabel.setForeground(TEXT_DIM);
        rangeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(rangeLabel);

        return card;
    }

    private JComponent buildPlayCard() {
        RoundedPanel card = new RoundedPanel(18, CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ---- Attempts row ----
        JPanel attemptsRow = new JPanel(new BorderLayout());
        attemptsRow.setOpaque(false);
        attemptsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        attemptsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        attemptsText = new JLabel("Attempts: 0 / 0");
        attemptsText.setFont(FONT_BODY_B);
        attemptsText.setForeground(TEXT);
        attemptsRow.add(attemptsText, BorderLayout.WEST);

        attemptDots = new AttemptDots();
        JPanel dotsWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        dotsWrap.setOpaque(false);
        dotsWrap.add(attemptDots);
        attemptsRow.add(dotsWrap, BorderLayout.EAST);

        card.add(attemptsRow);
        card.add(Box.createVerticalStrut(16));

        // ---- New round button ----
        newRoundButton = new PillButton("Start New Round", PillButton.Style.PRIMARY);
        newRoundButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        newRoundButton.addActionListener(e -> startNewRound());
        newRoundButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        card.add(newRoundButton);
        card.add(Box.createVerticalStrut(16));

        // ---- Guess row ----
        JPanel guessRow = new JPanel();
        guessRow.setOpaque(false);
        guessRow.setLayout(new BoxLayout(guessRow, BoxLayout.X_AXIS));
        guessRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        guessRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        guessField = new RoundedTextField(18);
        guessField.setFont(new Font("SansSerif", Font.BOLD, 16));
        guessField.setEnabled(false);
        guessField.addActionListener(e -> handleGuess());

        guessButton = new PillButton("Guess", PillButton.Style.ACCENT);
        guessButton.setEnabled(false);
        guessButton.addActionListener(e -> handleGuess());
        guessButton.setPreferredSize(new Dimension(96, 42));

        guessRow.add(guessField);
        guessRow.add(Box.createHorizontalStrut(10));
        guessRow.add(guessButton);
        card.add(guessRow);
        card.add(Box.createVerticalStrut(16));

        // ---- Feedback card ----
        feedbackCard = new RoundedPanel(14, CARD_LIGHT);
        feedbackCard.setLayout(new BoxLayout(feedbackCard, BoxLayout.X_AXIS));
        feedbackCard.setBorder(new EmptyBorder(14, 16, 14, 16));
        feedbackCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        feedbackCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        feedbackIcon = new JLabel("\uD83D\uDD22");
        feedbackIcon.setFont(new Font("SansSerif", Font.PLAIN, 22));
        feedbackLabel = new JLabel("Choose a difficulty and start a round.");
        feedbackLabel.setFont(FONT_FEEDBACK.deriveFont(16f));
        feedbackLabel.setForeground(TEXT_DIM);

        feedbackCard.add(feedbackIcon);
        feedbackCard.add(Box.createHorizontalStrut(12));
        feedbackCard.add(feedbackLabel);

        card.add(feedbackCard);
        return card;
    }

    private JComponent buildHistoryCard() {
        RoundedPanel card = new RoundedPanel(18, CARD);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headRow = new JPanel(new BorderLayout());
        headRow.setOpaque(false);
        headRow.add(sectionLabel("ROUND HISTORY"), BorderLayout.WEST);

        statsLabel = new JLabel("0 wins / 0 rounds");
        statsLabel.setFont(FONT_BODY);
        statsLabel.setForeground(TEXT_DIM);
        headRow.add(statsLabel, BorderLayout.EAST);
        card.add(headRow, BorderLayout.NORTH);

        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        historyList.setOpaque(false);
        historyList.setCellRenderer(new HistoryRenderer());
        historyList.setFixedCellHeight(46);
        historyList.setSelectionModel(new javax.swing.DefaultListSelectionModel() {
            @Override public void setSelectionInterval(int i0, int i1) { /* not selectable */ }
        });

        JScrollPane scroll = new JScrollPane(historyList);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        styleScrollBar(scroll.getVerticalScrollBar());
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private static void styleScrollBar(JScrollBar bar) {
        bar.setPreferredSize(new Dimension(8, 0));
        bar.setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                this.thumbColor = ACCENT_DIM;
                this.trackColor = CARD;
            }
            @Override protected JButton createDecreaseButton(int o) { return zeroButton(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroButton(); }
            private JButton zeroButton() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                return b;
            }
        });
    }

    private static JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(ACCENT.brighter());
        return l;
    }

    // =====================================================================
    // Game logic
    // =====================================================================

    private void selectDifficulty(Difficulty d) {
        if (roundActive) return; // locked mid-round
        currentDifficulty = d;
        Difficulty[] all = Difficulty.values();
        for (int i = 0; i < all.length; i++) {
            difficultyButtons[i].setSelected(all[i] == d);
        }
        rangeLabel.setText("Range " + d.min + "\u2013" + d.max + "   \u00B7   " + d.maxAttempts + " attempts max");
        attemptDots.setMax(d.maxAttempts);
        attemptDots.setFilled(0);
        attemptsText.setText("Attempts: 0 / " + d.maxAttempts);
    }

    private void startNewRound() {
        secretNumber = random.nextInt(currentDifficulty.max - currentDifficulty.min + 1) + currentDifficulty.min;
        attemptsUsed = 0;
        roundNumber++;
        roundActive = true;

        for (PillButton b : difficultyButtons) b.setEnabled(false);
        attemptDots.setMax(currentDifficulty.maxAttempts);
        attemptDots.setFilled(0);
        attemptsText.setText("Attempts: 0 / " + currentDifficulty.maxAttempts);

        setFeedback("\uD83E\uDD14", "Guess a number between " + currentDifficulty.min
                + " and " + currentDifficulty.max, TEXT, CARD_LIGHT);

        guessField.setText("");
        guessField.setEnabled(true);
        guessButton.setEnabled(true);
        newRoundButton.setEnabled(false);
        guessField.requestFocusInWindow();
    }

    private void handleGuess() {
        if (!roundActive) return;

        String text = guessField.getText().trim();
        int guess;
        try {
            guess = Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            setFeedback("\u26A0\uFE0F", "Please enter a valid whole number.", DANGER, CARD_LIGHT);
            return;
        }

        if (guess < currentDifficulty.min || guess > currentDifficulty.max) {
            setFeedback("\u26A0\uFE0F", "Enter a number between " + currentDifficulty.min
                    + " and " + currentDifficulty.max + "!", DANGER, CARD_LIGHT);
            return;
        }

        attemptsUsed++;
        attemptsText.setText("Attempts: " + attemptsUsed + " / " + currentDifficulty.maxAttempts);
        attemptDots.setFilled(attemptsUsed);

        if (guess == secretNumber) {
            setFeedback("\uD83C\uDF89", "Correct! The number was " + secretNumber + ".", SUCCESS, blend(SUCCESS));
            endRound(true);
        } else if (guess < secretNumber) {
            setFeedback("\u2B06\uFE0F", "Too Low! Try higher.", INFO, blend(INFO));
            checkAttemptsExhausted();
        } else {
            setFeedback("\u2B07\uFE0F", "Too High! Try lower.", WARN, blend(WARN));
            checkAttemptsExhausted();
        }

        guessField.setText("");
        guessField.requestFocusInWindow();
    }

    private void checkAttemptsExhausted() {
        if (attemptsUsed >= currentDifficulty.maxAttempts) {
            setFeedback("\uD83D\uDC80", "You Lost! The number was " + secretNumber + ".", DANGER, blend(DANGER));
            endRound(false);
        }
    }

    private void endRound(boolean won) {
        roundActive = false;
        guessField.setEnabled(false);
        guessButton.setEnabled(false);
        newRoundButton.setEnabled(true);
        for (PillButton b : difficultyButtons) b.setEnabled(true);

        if (won) wins++;
        RoundRecord record = new RoundRecord(roundNumber, currentDifficulty, won,
                secretNumber, attemptsUsed, currentDifficulty.maxAttempts);
        historyModel.add(0, record); // newest first
        statsLabel.setText(wins + " win" + (wins == 1 ? "" : "s") + " / " + roundNumber
                + " round" + (roundNumber == 1 ? "" : "s"));

        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    (won ? "You guessed it! " : "Better luck next time! ") + "Play again?",
                    "Play Again?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                startNewRound();
            } else {
                setFeedback("\uD83D\uDC4B", "Thanks for playing! Final round: " + roundNumber + ".", TEXT_DIM, CARD_LIGHT);
            }
        });
    }

    private void setFeedback(String icon, String text, Color fg, Color bg) {
        feedbackIcon.setText(icon);
        feedbackLabel.setText(text);
        feedbackLabel.setForeground(fg);
        feedbackCard.setBackground(bg);
        feedbackCard.repaint();
    }

    private static Color blend(Color c) {
        // A dim, translucent-looking tint of the accent color over the card background.
        int r = (c.getRed() + CARD_LIGHT.getRed() * 3) / 4;
        int g = (c.getGreen() + CARD_LIGHT.getGreen() * 3) / 4;
        int b = (c.getBlue() + CARD_LIGHT.getBlue() * 3) / 4;
        return new Color(r, g, b);
    }

    // =====================================================================
    // Custom components
    // =====================================================================

    /** A JPanel painted with rounded corners on a solid background color. */
    private static class RoundedPanel extends JPanel {
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

    /** A rounded, borderless text field matching the dark theme. */
    private static class RoundedTextField extends JTextField {
        RoundedTextField(int cols) {
            super(cols);
            setOpaque(false);
            setForeground(TEXT);
            setCaretColor(TEXT);
            setBackground(CARD_LIGHT);
            setBorder(new EmptyBorder(8, 14, 8, 14));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isEnabled() ? CARD_LIGHT : BG);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            g2.setColor(isFocusOwner() ? ACCENT : new Color(0x3A, 0x35, 0x52));
            g2.setStroke(new BasicStroke(1.4f));
            g2.draw(new RoundRectangle2D.Float(0.7f, 0.7f, getWidth() - 1.4f, getHeight() - 1.4f, 12, 12));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** A flat, rounded button with hover/selected states, styled per role. */
    private static class PillButton extends JButton {
        enum Style { PRIMARY, ACCENT, SEGMENT }

        private final Style style;
        private boolean hover = false;
        private boolean selected = false;

        PillButton(String text, Style style) {
            super(text);
            this.style = style;
            setFont(FONT_BODY_B);
            setForeground(TEXT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 14, 8, 14));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        public void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg;
            Color fg = TEXT;
            if (!isEnabled()) {
                bg = style == Style.SEGMENT ? CARD_LIGHT : new Color(0x2A, 0x27, 0x3D);
                fg = TEXT_DIM;
            } else if (style == Style.SEGMENT) {
                bg = selected ? ACCENT : (hover ? CARD_LIGHT : CARD_LIGHT.darker());
                fg = selected ? Color.WHITE : TEXT_DIM;
            } else if (style == Style.PRIMARY) {
                bg = hover ? ACCENT.darker() : ACCENT_DIM;
            } else { // ACCENT
                bg = hover ? ACCENT.brighter() : ACCENT;
            }

            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            g2.dispose();

            setForeground(fg);
            super.paintComponent(g);
        }
    }

    /** Row of small circles showing attempts used vs. remaining. */
    private static class AttemptDots extends JComponent {
        private int max = 7;
        private int filled = 0;
        private static final int SIZE = 11;
        private static final int GAP = 6;

        void setMax(int max) {
            this.max = Math.max(1, max);
            revalidate();
            repaint();
        }

        void setFilled(int filled) {
            this.filled = filled;
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(max * SIZE + (max - 1) * GAP, SIZE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 0; i < max; i++) {
                int x = i * (SIZE + GAP);
                boolean used = i < filled;
                double ratio = (double) filled / max;
                Color c;
                if (!used) {
                    c = new Color(0x39, 0x34, 0x50);
                } else if (ratio <= 0.5) {
                    c = SUCCESS;
                } else if (ratio <= 0.8) {
                    c = WARN;
                } else {
                    c = DANGER;
                }
                g2.setColor(c);
                g2.fillOval(x, 0, SIZE, SIZE);
            }
            g2.dispose();
        }
    }

    /** Renders each RoundRecord as a small colored card with a WON/LOST badge. */
    private class HistoryRenderer extends JPanel implements ListCellRenderer<RoundRecord> {
        private final JLabel badge = new JLabel();
        private final JLabel title = new JLabel();
        private final JLabel subtitle = new JLabel();
        private final RoundedPanel badgeWrap = new RoundedPanel(8, SUCCESS);

        HistoryRenderer() {
            setOpaque(false);
            setLayout(new BorderLayout(10, 0));
            setBorder(new EmptyBorder(4, 2, 4, 2));

            JPanel textStack = new JPanel();
            textStack.setOpaque(false);
            textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
            title.setFont(FONT_BODY_B);
            title.setForeground(TEXT);
            subtitle.setFont(FONT_SUB);
            subtitle.setForeground(TEXT_DIM);
            textStack.add(title);
            textStack.add(subtitle);

            badgeWrap.setLayout(new BorderLayout());
            badge.setFont(FONT_MONO);
            badge.setForeground(Color.WHITE);
            badge.setBorder(new EmptyBorder(4, 10, 4, 10));
            badgeWrap.add(badge, BorderLayout.CENTER);

            add(textStack, BorderLayout.CENTER);
            add(badgeWrap, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends RoundRecord> list, RoundRecord r,
                                                        int index, boolean isSelected, boolean cellHasFocus) {
            title.setText("Round " + r.roundNumber + " \u00B7 " + r.difficulty.label);
            if (r.won) {
                subtitle.setText("Guessed " + r.secretNumber + " in " + r.attemptsUsed
                        + " attempt" + (r.attemptsUsed == 1 ? "" : "s"));
                badge.setText("WON");
                badgeWrap.setBackground(SUCCESS);
            } else {
                subtitle.setText("Number was " + r.secretNumber + " \u00B7 used " + r.attemptsUsed + "/" + r.maxAttempts);
                badge.setText("LOST");
                badgeWrap.setBackground(DANGER);
            }
            return this;
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
            NumberGuessingGame game = new NumberGuessingGame();
            game.setVisible(true);
        });
    }
}
