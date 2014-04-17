package uk.co.metro.carousel.images;

public class ImageConfig {
	private int deviceWidth = 300;
	private int deviceHeight = 400;
	private ImageQuality quality = ImageQuality.Low;
	
	public ImageConfig(int deviceWidth, int deviceHeight) {
		this.deviceWidth = deviceWidth;
		this.deviceHeight = deviceHeight;
	}
	
	public ImageConfig(int deviceWidth, int deviceHeight, ImageQuality quality) {
		this(deviceWidth, deviceHeight);
		this.quality = quality;
	}
	
	public int getDeviceWidth() {
		return deviceWidth;
	}
	public void setDeviceWidth(int deviceWidth) {
		this.deviceWidth = deviceWidth;
	}
	
	public int getDeviceHeight() {
		return deviceHeight;
	}
	public void setDeviceHeight(int deviceHeight) {
		this.deviceHeight = deviceHeight;
	}

	public ImageQuality getQuality() {
		return quality;
	}
	public void setQuality(ImageQuality quality) {
		this.quality = quality;
	}
	
}
