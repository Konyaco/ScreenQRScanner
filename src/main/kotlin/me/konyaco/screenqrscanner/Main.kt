package me.konyaco.screenqrscanner

import com.google.zxing.BinaryBitmap
import com.google.zxing.NotFoundException
import com.google.zxing.Result
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import java.awt.*
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.*
import kotlin.system.exitProcess

private val iconImg: BufferedImage = ImageIO.read(ClassLoader.getSystemResources("outline_qr_code_scanner_white_24dp.png").nextElement())

fun main() {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    val tray = SystemTray.getSystemTray()
    val popupMenu = PopupMenu("Scanner").apply {
        add(MenuItem("Scan").apply {
            addActionListener { scanAndShow() }
        })
        add(MenuItem("Quit").apply {
            addActionListener { exitProcess(0) }
        })
    }
    tray.add(
        TrayIcon(
            iconImg,
            "Screen QR Scanner",
            popupMenu
        ).apply {
            isImageAutoSize = true
            addActionListener { scanAndShow() }
        }
    )
}

private fun scanAndShow() {
    val frame = JFrame().apply {
        iconImage = iconImg
        defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        layout = FlowLayout()
    }
    val result = try {
        scan()
    } catch (e: Exception) {
        frame.apply {
            title = "Error"
            add(JLabel(e.message))
            add(JButton("Confirm").apply { addActionListener { dispose() } })
            pack()
            setLocationRelativeTo(null)
            isVisible = true
        }
        return
    }
    if (result == null) {
        frame.apply {
            title = "QR Not found"
            add(JLabel("QR Not Found"))
            add(JButton("Confirm").apply { addActionListener { dispose() } })
            pack()
            setLocationRelativeTo(null)
            isVisible = true
        }
    } else {
        frame.apply {
            title = "Result"
            add(JTextField(result.text))
            add(JButton("Confirm").apply { addActionListener { dispose() } })
            pack()
            setLocationRelativeTo(null)
            isVisible = true
        }
    }
}

private fun scan(): Result? {
    val screenshot =
        Robot().createScreenCapture(Toolkit.getDefaultToolkit().screenSize.let { Rectangle(it.width, it.height) })
    val img = BinaryBitmap(HybridBinarizer(BufferedImageLuminanceSource(screenshot)))
    return try {
        QRCodeReader().decode(img)!!
    } catch (e: NotFoundException) {
        null
    }
}