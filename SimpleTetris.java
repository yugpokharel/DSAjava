import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;


public class SimpleTetris extends JFrame {
    // Game constants
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int BLOCK_SIZE = 25;
    
    // Game variables
    private Color[][] board = new Color[BOARD_HEIGHT][BOARD_WIDTH];
    private Queue<int[][]> blockQueue = new LinkedList<>();
    private Stack<Color[]> boardStack = new Stack<>();
    private int[][] currentBlock;
    private Color currentColor;
    private int currentX, currentY;
    private Timer timer;
    private int score = 0;
    private boolean gameOver = false;
    
    // UI components
    private JPanel gamePanel;
    private JPanel previewPanel;
    private JLabel scoreLabel;
    
    // Block shapes and colors
    private static final int[][][] SHAPES = {
        {{1,1,1,1}},                         // I shape
        {{1,1,1}, {1,0,0}},                  // J shape
        {{1,1,1}, {0,0,1}},                  // L shape
        {{1,1}, {1,1}},                      // O shape
        {{0,1,1}, {1,1,0}},                  // S shape
        {{1,1,1}, {0,1,0}},                  // T shape
        {{1,1,0}, {0,1,1}}                   // Z shape
    };
    
    private static final Color[] COLORS = {
        Color.CYAN, Color.BLUE, Color.ORANGE, Color.YELLOW, 
        Color.GREEN, Color.MAGENTA, Color.RED
    };
    
    public SimpleTetris() {
        setTitle("Simple Tetris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Initialize game panel
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
                drawCurrentBlock(g);
            }
        };
        gamePanel.setPreferredSize(new Dimension(BOARD_WIDTH * BLOCK_SIZE, BOARD_HEIGHT * BLOCK_SIZE));
        gamePanel.setBackground(Color.BLACK);
        add(gamePanel, BorderLayout.CENTER);
        
        // Initialize side panel
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        
        // Preview panel
        previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawNextBlock(g);
            }
        };
        previewPanel.setPreferredSize(new Dimension(4 * BLOCK_SIZE, 4 * BLOCK_SIZE));
        previewPanel.setBackground(Color.BLACK);
        sidePanel.add(new JLabel("Next Block:"));
        sidePanel.add(previewPanel);
        
        // Score display
        scoreLabel = new JLabel("Score: 0");
        sidePanel.add(scoreLabel);
        
        // Control buttons
        JPanel controlPanel = new JPanel(new GridLayout(1, 3));
        JButton leftButton = new JButton("←");
        JButton rotateButton = new JButton("Rotate");
        JButton rightButton = new JButton("→");
        
        leftButton.addActionListener(e -> moveLeft());
        rotateButton.addActionListener(e -> rotate());
        rightButton.addActionListener(e -> moveRight());
        
        controlPanel.add(leftButton);
        controlPanel.add(rotateButton);
        controlPanel.add(rightButton);
        sidePanel.add(controlPanel);
        
        // Start button
        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(e -> startGame());
        sidePanel.add(startButton);
        
        add(sidePanel, BorderLayout.EAST);
        
        // Key listeners
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver) return;
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT: moveLeft(); break;
                    case KeyEvent.VK_RIGHT: moveRight(); break;
                    case KeyEvent.VK_UP: rotate(); break;
                    case KeyEvent.VK_DOWN: moveDown(); break;
                    case KeyEvent.VK_SPACE: dropDown(); break;
                }
            }
        });
        
        setFocusable(true);
        pack();
        setLocationRelativeTo(null);
    }
    
    private void startGame() {
        // Initialize game state
        board = new Color[BOARD_HEIGHT][BOARD_WIDTH];
        blockQueue.clear();
        boardStack.clear();
        score = 0;
        gameOver = false;
        scoreLabel.setText("Score: 0");
        
        // Initialize board stack with empty rows
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            boardStack.push(new Color[BOARD_WIDTH]);
        }
        
        // Generate initial blocks
        generateBlock();
        spawnBlock();
        
        // Start game timer
        if (timer != null) timer.stop();
        timer = new Timer(500, e -> {
            if (!moveDown()) {
                placeBlock();
                clearLines();
                spawnBlock();
                
                if (isGameOver()) {
                    timer.stop();
                    gameOver = true;
                    JOptionPane.showMessageDialog(this, "Game Over! Score: " + score);
                }
            }
            gamePanel.repaint();
            previewPanel.repaint();
        });
        timer.start();
        requestFocus();
    }
    
    private void generateBlock() {
        int index = (int)(Math.random() * SHAPES.length);
        blockQueue.add(SHAPES[index]);
    }
    
    private void spawnBlock() {
        if (blockQueue.isEmpty()) {
            generateBlock();
        }
        
        currentBlock = blockQueue.poll();
        currentColor = COLORS[(int)(Math.random() * COLORS.length)];
        currentX = BOARD_WIDTH / 2 - currentBlock[0].length / 2;
        currentY = 0;
        
        generateBlock(); // Generate next block
    }
    
    private boolean moveLeft() {
        if (isValidMove(currentX - 1, currentY, currentBlock)) {
            currentX--;
            gamePanel.repaint();
            return true;
        }
        return false;
    }
    
    private boolean moveRight() {
        if (isValidMove(currentX + 1, currentY, currentBlock)) {
            currentX++;
            gamePanel.repaint();
            return true;
        }
        return false;
    }
    
    private boolean moveDown() {
        if (isValidMove(currentX, currentY + 1, currentBlock)) {
            currentY++;
            gamePanel.repaint();
            return true;
        }
        return false;
    }
    
    private void dropDown() {
        while (moveDown()) {
            // Keep moving down until collision
        }
    }
    
    private void rotate() {
        int[][] rotated = rotateBlock(currentBlock);
        if (isValidMove(currentX, currentY, rotated)) {
            currentBlock = rotated;
            gamePanel.repaint();
        }
    }
    
    private int[][] rotateBlock(int[][] block) {
        int rows = block.length;
        int cols = block[0].length;
        int[][] rotated = new int[cols][rows];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][rows - 1 - i] = block[i][j];
            }
        }
        
        return rotated;
    }
    
    private boolean isValidMove(int x, int y, int[][] shape) {
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 0) continue;
                
                int newX = x + j;
                int newY = y + i;
                
                if (newX < 0 || newX >= BOARD_WIDTH || newY >= BOARD_HEIGHT) {
                    return false;
                }
                
                if (newY >= 0 && board[newY][newX] != null) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void placeBlock() {
        for (int i = 0; i < currentBlock.length; i++) {
            for (int j = 0; j < currentBlock[i].length; j++) {
                if (currentBlock[i][j] == 0) continue;
                
                int boardX = currentX + j;
                int boardY = currentY + i;
                
                if (boardY >= 0 && boardY < BOARD_HEIGHT) {
                    board[boardY][boardX] = currentColor;
                }
            }
        }
        
        // Update board stack
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            Color[] row = boardStack.get(y);
            System.arraycopy(board[y], 0, row, 0, BOARD_WIDTH);
        }
    }
    
    private void clearLines() {
        int linesCleared = 0;
        
        for (int y = BOARD_HEIGHT - 1; y >= 0; y--) {
            boolean lineIsFull = true;
            
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (board[y][x] == null) {
                    lineIsFull = false;
                    break;
                }
            }
            
            if (lineIsFull) {
                linesCleared++;
                
                // Remove the line from stack
                boardStack.remove(y);
                
                // Add new empty line at top
                boardStack.add(0, new Color[BOARD_WIDTH]);
                
                // Update board from stack
                for (int i = 0; i < BOARD_HEIGHT; i++) {
                    System.arraycopy(boardStack.get(i), 0, board[i], 0, BOARD_WIDTH);
                }
                
                y++; // Check the same row again
            }
        }
        
        if (linesCleared > 0) {
            score += linesCleared * 100;
            scoreLabel.setText("Score: " + score);
        }
    }
    
    private boolean isGameOver() {
        // Check if blocks reach the top
        for (int x = 0; x < BOARD_WIDTH; x++) {
            if (board[0][x] != null) {
                return true;
            }
        }
        return false;
    }
    
    private void drawBoard(Graphics g) {
        // Draw grid
        g.setColor(Color.DARK_GRAY);
        for (int x = 0; x <= BOARD_WIDTH; x++) {
            g.drawLine(x * BLOCK_SIZE, 0, x * BLOCK_SIZE, BOARD_HEIGHT * BLOCK_SIZE);
        }
        for (int y = 0; y <= BOARD_HEIGHT; y++) {
            g.drawLine(0, y * BLOCK_SIZE, BOARD_WIDTH * BLOCK_SIZE, y * BLOCK_SIZE);
        }
        
        // Draw placed blocks
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (board[y][x] != null) {
                    drawBlock(g, x, y, board[y][x]);
                }
            }
        }
    }
    
    private void drawCurrentBlock(Graphics g) {
        if (currentBlock == null) return;
        
        for (int i = 0; i < currentBlock.length; i++) {
            for (int j = 0; j < currentBlock[i].length; j++) {
                if (currentBlock[i][j] == 0) continue;
                
                int x = currentX + j;
                int y = currentY + i;
                
                if (y >= 0) {
                    drawBlock(g, x, y, currentColor);
                }
            }
        }
    }
    
    private void drawNextBlock(Graphics g) {
        if (blockQueue.isEmpty()) return;
        
        int[][] nextBlock = blockQueue.peek();
        Color nextColor = COLORS[(int)(Math.random() * COLORS.length)];
        
        int offsetX = (4 - nextBlock[0].length) / 2;
        int offsetY = (4 - nextBlock.length) / 2;
        
        for (int i = 0; i < nextBlock.length; i++) {
            for (int j = 0; j < nextBlock[i].length; j++) {
                if (nextBlock[i][j] == 0) continue;
                
                int x = offsetX + j;
                int y = offsetY + i;
                
                drawBlock(g, x, y, nextColor);
            }
        }
    }
    
    private void drawBlock(Graphics g, int x, int y, Color color) {
        g.setColor(color);
        g.fillRect(x * BLOCK_SIZE + 1, y * BLOCK_SIZE + 1, BLOCK_SIZE - 2, BLOCK_SIZE - 2);
        
        // Add 3D effect
        g.setColor(color.brighter());
        g.drawLine(x * BLOCK_SIZE + 1, y * BLOCK_SIZE + 1, 
                   x * BLOCK_SIZE + BLOCK_SIZE - 2, y * BLOCK_SIZE + 1);
        g.drawLine(x * BLOCK_SIZE + 1, y * BLOCK_SIZE + 1, 
                   x * BLOCK_SIZE + 1, y * BLOCK_SIZE + BLOCK_SIZE - 2);
        
        g.setColor(color.darker());
        g.drawLine(x * BLOCK_SIZE + BLOCK_SIZE - 2, y * BLOCK_SIZE + 1, 
                   x * BLOCK_SIZE + BLOCK_SIZE - 2, y * BLOCK_SIZE + BLOCK_SIZE - 2);
        g.drawLine(x * BLOCK_SIZE + 1, y * BLOCK_SIZE + BLOCK_SIZE - 2, 
                   x * BLOCK_SIZE + BLOCK_SIZE - 2, y * BLOCK_SIZE + BLOCK_SIZE - 2);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimpleTetris game = new SimpleTetris();
            game.setVisible(true);
        });
    }
}

