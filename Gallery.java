import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Gallery extends Frame implements ActionListener {

    private BufferedImage image;
    private double scale = 1.0;
    private int rotation = 0;

    private Panel controlPanel;
    private Canvas imageCanvas;

    private File[] imageFiles;
    private int currentIndex = -1;

    public Gallery() {  // Changed from ImageViewerAWT to Gallery
        super("Gallery");

        // Setup buttons
        controlPanel = new Panel();
        String[] buttons = {"Open", "Previous", "Next", "Zoom In", "Zoom Out", "Rotate", "Save"};
        for (String label : buttons) {
            Button btn = new Button(label);
            btn.addActionListener(this);
            controlPanel.add(btn);
        }
        add(controlPanel, BorderLayout.SOUTH);

        // Setup canvas for image display
        imageCanvas = new Canvas() {
            public void paint(Graphics g) {
                if (image != null) {
                    Graphics2D g2 = (Graphics2D) g;

                    // High-quality rendering
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = image.getWidth();
                    int h = image.getHeight();
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;

                    g2.translate(cx, cy);
                    g2.rotate(Math.toRadians(rotation));
                    g2.scale(scale, scale);
                    g2.drawImage(image, -w / 2, -h / 2, null);
                }
            }
        };
        add(imageCanvas, BorderLayout.CENTER);

        setSize(800, 600);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "Open":
                FileDialog fd = new FileDialog(this, "Choose an image", FileDialog.LOAD);
                fd.setVisible(true);
                if (fd.getFile() != null) {
                    File selectedFile = new File(fd.getDirectory(), fd.getFile());
                    File folder = selectedFile.getParentFile();
                    imageFiles = folder.listFiles((dir, name) -> {
                        String lower = name.toLowerCase();
                        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") ||
                               lower.endsWith(".bmp") || lower.endsWith(".gif");
                    });
                    for (int i = 0; i < imageFiles.length; i++) {
                        if (imageFiles[i].getName().equals(selectedFile.getName())) {
                            currentIndex = i;
                            break;
                        }
                    }
                    loadImage();
                }
                break;

            case "Previous":
                if (imageFiles != null && currentIndex > 0) {
                    currentIndex--;
                    loadImage();
                }
                break;

            case "Next":
                if (imageFiles != null && currentIndex < imageFiles.length - 1) {
                    currentIndex++;
                    loadImage();
                }
                break;

            case "Zoom In":
                scale += 0.1;
                imageCanvas.repaint();
                break;

            case "Zoom Out":
                scale = Math.max(0.1, scale - 0.1);
                imageCanvas.repaint();
                break;

            case "Rotate":
                rotation = (rotation + 90) % 360;
                imageCanvas.repaint();
                break;

            case "Save":
                if (image != null) {
                    FileDialog saveDialog = new FileDialog(this, "Save Image", FileDialog.SAVE);
                    saveDialog.setVisible(true);
                    if (saveDialog.getFile() != null) {
                        String savePath = saveDialog.getDirectory() + saveDialog.getFile();
                        try {
                            ImageIO.write(image, "png", new File(savePath));
                            System.out.println("Image saved to: " + savePath);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                break;
        }
    }

    private void loadImage() {
        try {
            image = ImageIO.read(imageFiles[currentIndex]);
            scale = 1.0;
            rotation = 0;
            imageCanvas.repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Gallery();  // Changed from ImageViewerAWT to Gallery
    }
}
