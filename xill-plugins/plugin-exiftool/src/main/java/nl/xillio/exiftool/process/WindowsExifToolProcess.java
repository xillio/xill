package nl.xillio.exiftool.process;


public class WindowsExifToolProcess extends AbstractExifToolProcess {
    public WindowsExifToolProcess() {
        super(nativeBinary, embeddedBinaryPath);
    }
}
