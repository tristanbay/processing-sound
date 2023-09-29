package processing.sound;

import com.jsyn.unitgen.ChannelOut;

/**
 * Controls the routing of sounds on multi-channel devices
 * 
 * @webref I/O:MultiChannel
 */
public abstract class MultiChannel {

	/**
	 * Controls which output channel sounds will be played back to.
	 *
	 * After selecting a new output channel, all sounds that start `play()`ing 
	 * will be sent to that channel.
	 * 
	 * @param channel the channel number to send sounds to
	 * @return the channel number that sounds will be sent to
	 *
	 * @webref I/O:MultiChannel
	 * @see MultiChannel#availableChannels()
	 */
	public static int activeChannel(int channel) {
		Engine.getEngine().selectOutputChannel(channel);
		return MultiChannel.activeChannel();
	}

	public static int activeChannel() {
		return Engine.getEngine().outputChannel;
	}

	/**
	 * Connect a SoundObject to the given output channel.
	 *
	 * Use this only for SoundObjects that are already playing back on some 
	 * channel, which you want to have playing back on another channel at the same 
	 * time.
	 */
	public static void connectToOutput(SoundObject o, int channel) {
		Engine.getEngine().connectToOutput(channel, o.circuit);
	}

	/**
	 * Disconnect a SoundObject from the given output channel.
	 *
	 * Only use on SoundObjects that were previously connected using 
	 * connectToOutput()
	 *
	 * @see connectToOutput()
	 */
	public static void disconnectFromOutput(SoundObject o, int channel) {
		Engine.getEngine().disconnectFromOutput(channel, o.circuit);
	}

	/**
	 * Gets the number of output channels available on an output device
	 * 
	 * @param deviceId if none is given, gets information about the current device.
	 * @return the number of output channels available on the current output device
	 *
	 * @webref I/O:MultiChannel
	 * @see Sound#outputDevice(int)
	 */
	public static int availableChannels(int deviceId) {
		return Engine.getAudioDeviceManager().getMaxOutputChannels(deviceId);
	}

	public static int availableChannels() {
		return MultiChannel.availableChannels(Engine.getEngine().outputDevice);
	}

	/**
	 * Returns the JSyn <code>ChannelOut</code> objects that are being played to 
	 * by the synthesizer
	 * @see ChannelOut
	 */
	public static ChannelOut[] outputs() {
		return Engine.getEngine().output;
	}

	/**
	 * Force using PortAudio instead of JavaSound.
	 *
	 * Support for 24 bit audio interfaces on Windows requires using the native
	 * PortAudio bindings instead of the default JavaSound one. The Sound library 
	 * will automatically check for and load PortAudio when it is necessary to do 
	 * so. However, when <code>Sound.list()</code> is called before selecting an 
	 * output device, it might show an incorrect number of channels for 
	 * multi-channel interfaces. By explicitly loading PortAudio ahead of time you 
	 * can ensure that <code>Sound.list()</code> will show accurate channel 
	 * numbers from the start.
	 *
	 * Returns <code>true</code> if PortAudio was successfully loaded.
	 *
	 * @webref I/O:MultiChannel
	 * @webBrief Force using PortAudio instead of JavaSound.
	 * @see Sound#list()
	 */
	public static boolean usePortAudio() {
		Engine engine = Engine.getEngine(null, true);
		try {
			if (engine.usePortAudio(true)) {
				// all good, go for default device
				engine.selectOutputDevice(-1);
			}
			return engine.isUsingPortAudio();
		} catch (RuntimeException e) {
			Engine.printError(e.getMessage());
		}
		return engine.isUsingPortAudio();
	}

}
