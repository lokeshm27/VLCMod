import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.io.FilenameUtils;

public class Operations {
	String Dir = new String("C:\\ProgramData\\VLCMod\\");
	String Bin = new String(Dir + "Bin\\");
	String Data = new String(Dir + "Data\\");
	String Cache = new String(Dir + "Cache\\");
	String Files = new String(Bin + "Files\\");
	File runFlag = new File("C:\\ProgramData\\VLCMod\\Cache\\Running.dat");
	File curPlayingFlag = new File(Cache + "curPlayingFlag.dat");
	public long Border = 2000;
	static Controls c = new Controls();
	static int PORT = 8082;
	static Settings Dfault;
	public final CountDownLatch latch = new CountDownLatch(1);
	public final CountDownLatch latch2 = new CountDownLatch(1);
	public final CountDownLatch latch3 = new CountDownLatch(1);
	public final CountDownLatch latch4 = new CountDownLatch(1);
	static int ret;
	Date Session;
	

	public Operations() {
		try {
			File set = new File(Data + "Settings.dat");
			if (!set.exists()) {
				Dfault = new Settings(2, 2, true);
			} else {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(set));
				Dfault = (Settings) ois.readObject();
				ois.close();
			}
		} catch (Exception e) {
			errorHandler(126, e.getMessage(), "Constructor - Operations", "Operations", true);
		}
	}

	public String encode(String Path) {
		Path = Path.replace(":", "%SE01%");
		Path = Path.replace("\\", "%SE02%");
		return Path;
	}

	public String decode(String Path) {
		Path = Path.replace("%SE01%", ":");
		Path = Path.replace("%SE02%", "\\");
		return Path;
	}

	public void setrunFlag() {
		try {
			Date date = new Date();
			Session = date;
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(runFlag));
			oos.writeObject(date);
			oos.close();
		} catch (Exception e) {
			errorHandler(102, e.getMessage(), "setrunFlag", "Operations", true);
		}
	}

	public boolean isUnderBorder(Date newDate) {
		Date oldDate = getrunFLag();
		long diff = newDate.getTime() - oldDate.getTime();
		if (diff <= Border) {
			return true;
		} else {
			return false;
		}
	}

	public Date getrunFLag() {
		Date d;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(runFlag));
			d = (Date) ois.readObject();
			ois.close();
			return d;
		} catch (Exception e) {
			d = null;
			errorHandler(103, e.getMessage(), "getrunFlag", "Operations", true);
		}
		return d;
	}

	public void errorHandler(int errorCode, String msg, String method, String clas, boolean showMessage) {
		try {
			if (runFlag.exists()) {
				runFlag.delete();
			}
			if (curPlayingFlag.exists()) {
				curPlayingFlag.delete();
			}
			File errorLog = new File(Bin + "errorLog.txt");
			Date date = new Date();
			PrintWriter pw = new PrintWriter(new FileOutputStream(errorLog, true));
			pw.println("");
			pw.println(" ----------------------- Error Description Start ----------------------------------------");
			pw.println("     Date  :  " + date);
			pw.println("Error Code :  " + errorCode);
			pw.println("   Message :  " + msg);
			pw.println("   Method  :  " + method);
			pw.println("     Class :  " + clas);
			pw.println(" ----------------------- Error Description End ------------------------------------------");
			pw.println("");
			pw.close();
			if (showMessage) {
				JOptionPane.showMessageDialog(null, "An Error occured in VLCMod,\n ErrorLog has been Saved",
						"VLCMod Error - " + errorCode, 0);
			}
			System.exit(0);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Two or More Errors Occured. Error Logs Can not be saved.",
					"VLCMod Error - 101", JOptionPane.ERROR_MESSAGE);
		}
		System.exit(-1);
	}

	public void add(String Path) {
		if (itsWorth(Path)) {
			setWorth(Path);
			c.add(Path);
		}
	}

	public void clearPlayList() {
		String Dir = Cache + "PlayList";
		File PlayDir = new File(Dir);
		if (PlayDir.exists()) {
			File list[] = PlayDir.listFiles();
			for (int i = 0; i < list.length; i++) {
				list[i].delete();
			}
			PlayDir.delete();
		}
	}

	public void process(Package P) {
		if (P.command.equals("playLast")) {
			Video V = getLastPlayed(P.File);
			c.add(V.Path);
		} else {
			if (P.command.equals("add") || P.command.equals("play")) {
				c.add(P.File);
			} else {
				c.enqueue(P.File);
			}
		}
	}

	@SuppressWarnings("resource")
	public String getVLCPath() {
		try {
			File data = new File(Bin + "Path.dat");
			if (!(data.exists())) {
				Desktop.getDesktop().open(new File("C:\\ProgramData\\VLCMod\\PathFinder.exe"));
				int i = 0;
				while (i < 300 && !(data.exists())) {
					i++;
					Thread.sleep(1000);
				}
				if (data.exists()) {
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(data));
					String ret = (String) ois.readObject();
					return ret;
				} else {
					onClose();
				}
			} else {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(data));
				String ret = (String) ois.readObject();
				return ret;
			}
		} catch (Exception e) {
			errorHandler(108, e.getMessage(), "getVLCPath", "Operations", true);
		}
		return null;
	}

	public boolean itsWorth(String Path) {
		String Dir = Cache + "PlayList";
		File PlayDir = new File(Dir);
		if (!(PlayDir.exists())) {
			PlayDir.mkdir();
			return true;
		} else {
			File file = new File(Dir + "\\" + encode(Path) + ".dat");
			if (file.exists()) {
				try {
					ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
					Date date = (Date) in.readObject();
					in.close();
					if (date.equals(Session)) {
						return false;
					} else {
						return true;
					}
				} catch (Exception e) {
					errorHandler(134, e.getMessage(), "itsWorth", "Operations", true);
				}
				return false;
			} else {
				return true;
			}
		}
	}

	public void setWorth(String Path) {
		String Dir = Cache + "PlayList";
		File PlayDir = new File(Dir);
		if (!(PlayDir.exists())) {
			PlayDir.mkdir();
		}
		File datFile = new File(Dir + "\\" + encode(Path) + ".dat");
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(datFile));
			out.writeObject(Session);
			out.close();
		} catch (Exception e) {
			errorHandler(135, e.getMessage(), "setWorth", "Operations", true);
		}
	}

	public void serial(Video v) {
		try {
			File vid = new File(v.Path);
			String parent = vid.getParent();
			String enPar = encode(parent);
			File enParDir = new File(Files + enPar);
			if (!enParDir.exists()) {
				enParDir.mkdir();
			}
			File data = new File(enParDir + "\\" + vid.getName() + ".dat");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(data));
			oos.writeObject(v);
			oos.close();
		} catch (Exception e) {
			errorHandler(122, e.getMessage(), "serial", "Operations", true);
		}
	}

	public Video deserial(String Path) {
		try {
			File vid = new File(Path);
			String Parent = vid.getParent();
			String enPar = encode(Parent);
			File enParDir = new File(Files + enPar);
			if (!enParDir.exists()) {
				enParDir.mkdir();
				return (new Video(Path, 0));
			}
			File data = new File(enParDir + "\\" + vid.getName() + ".dat");
			if (!data.exists()) {
				return (new Video(Path, 0));
			}
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(data));
			Video v = (Video) ois.readObject();
			ois.close();
			return v;
		} catch (Exception e) {
			errorHandler(123, e.getMessage(), "deserial", "Operations", true);
		}
		return null;
	}

	public void setLastPlayed(String File) {
		try {
			File Vi = new File(File);
			String Parent = Vi.getParent();
			String enCode = encode(Parent);
			File enParDir = new File(Files + enCode);
			if (!enParDir.exists()) {
				enParDir.mkdir();
			}
			File lastPlayed = new File(enParDir + "\\LastPlayed.dat");
			Video Vid = new Video(File, 0);
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(lastPlayed));
			oos.writeObject(Vid);
			oos.close();
		} catch (Exception e) {
			errorHandler(121, e.getMessage(), "setLastPlayed", "Operations", true);
		}
	}

	public Video getLastPlayed(String Folder) {
		try {
			String enPar = encode(Folder);
			File enParDir = new File(Files + enPar);
			if (!enParDir.exists()) {
				JOptionPane.showMessageDialog(null, "No Last Played Video in Folder \'" + Folder + "\' found.!",
						"Video not found", JOptionPane.ERROR_MESSAGE);
				onClose();
				return null;
			}
			File lastPlayed = new File(enParDir + "\\LastPlayed.dat");
			if (!lastPlayed.exists()) {
				JOptionPane.showMessageDialog(null, "No Last Played Video in Folder \'" + Folder + "\' found.!",
						"Video not found", JOptionPane.ERROR_MESSAGE);
				onClose();
				return null;
			}
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(lastPlayed));
			Video last = (Video) ois.readObject();
			ois.close();
			Video v = deserial(last.Path);
			return (v);
		} catch (Exception e) {
			errorHandler(125, e.getMessage(), "getLastPlayed", "Operations", true);
		}
		return null;
	}

	public int getTime(String File) {
		Video v = deserial(File);
		return (v.Time);
	}

	public boolean isPlayable(File file) {
		String ext = FilenameUtils.getExtension(file.getName());
		String[] formats = { "webm", "mkv", "flv", "vob", "ogv", "ogg", "drc", "gif", "gifv", "mng", "avi", "mov", "qt",
				"wmv", "yuv", "rm", "rmvb", "asf", "mp4", "m4p", "m4v", "mpg", "mp2", "mpeg", "mpe", "mpv", "svi",
				"3gp", "3g2", "mxf", "roq", "nsv", "f4p", "f4v", "f4a", "f4b" };
		for (int i = 0; i < formats.length; i++) {
			if (formats[i].equalsIgnoreCase(ext)) {
				return true;
			}
		}
		return false;
	}

	public void Monitor(String File) {
		Thread ContinPlay = new Thread(new Runnable() {
			@Override
			public void run() {
				if (Dfault.Contin) {
					File file = new File(File);
					File Par = file.getParentFile();
					File list[] = Par.listFiles();
					for (int i = (list.length) - 1; i >= 0; i--) {
						if (isPlayable(list[i])) {
							String Path = list[i].getPath();
							if (itsWorth(Path)) {
								setWorth(Path);
								add(Path);
							}
						}
					}
				}
			}
		});
		ContinPlay.setDaemon(true);
		ContinPlay.start();
		Video v = deserial(File);
		if (v.Time != 0) {
			if (Dfault.Resume) {
				c.seek(v.Time);
			} else if (Dfault.Ask) {
				int resp = frame1();
				if (resp == JOptionPane.YES_OPTION) {
					c.seek(v.Time);
				} else {
					c.seek(0);
				}
			} else {
				c.seek(0);
			}
		}

		Thread MonitorThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String MonitorFile = File;
					JFrame frame  = new JFrame("Monitoring - " + MonitorFile);
					frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					frame.setSize(400, 400);
					frame.setResizable(false);
					
					JTextArea text = new JTextArea();
					text.setEditable(false);
					text.setFont(new Font("Serif", Font.PLAIN, 15));
					text.setLineWrap(true);
					
					JScrollPane scroll = new JScrollPane(text);
					frame.add(scroll);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
					int Length = c.getLength();
					text.append("--- Monitoring '" + MonitorFile + "'----\n");
					text.append("\n");
					text.append("\n");
					text.append("Total Length of the Video = " + Length + "\n");
					text.append("\n");
					boolean ok = true;
					while (ok) {
						if (c.isPlaying()) {
							text.append("isPlaying = 1\n");
							String Title = c.getTitle();
							text.append("Title = " + Title + "\n");
							if (Title.equals(MonitorFile)) {
								text.append("Proceeding...\n");
								int time = c.getTime();
								if (time >= (Length - 4)){
									text.append("Since Time >= Length - 4, Serial(0)\n");
									text.append("\n");
									text.append("Itiration Complete...\n");
									text.append("\n");
									text.append("\n");
									serial(new Video(MonitorFile, 0));
									Thread.sleep(Dfault.performance * 1000);
								} else {
									text.append("Serail( " + time + ")\n");
									text.append("\n");
									text.append("Iteration Complete..\n");
									text.append("\n");
									text.append("\n");
									serial(new Video(MonitorFile, time));
									Thread.sleep(Dfault.performance * 1000);
								}
							} else {
								text.append("File Changed, Waiting for fileChangeHandler..\n");
								fileChangeHandler(Title);
								text.append("Got +ve response. Changing MonitorFile = " + Title + "\n");
								MonitorFile = Title;
								text.append("--- Monitoring " + MonitorFile + " ---\n");
								frame.setVisible(false);
								frame.setVisible(true);
							}
						} else {
							text.append("isPlaying = 0, Waiting..\n");
							text.append("\n");
							text.append("\n");
							Thread.sleep(Dfault.performance * 1000);
						}
					}
				} catch (Exception e) {
					errorHandler(128, e.getMessage(), "MonitorThread", "Operations", true);
				}
			}
		});
		MonitorThread.setDaemon(true);
		MonitorThread.start();
		try {
			latch4.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void onClose() {
		if (runFlag.exists()) {
			runFlag.delete();
		}
		if (curPlayingFlag.exists()) {
			curPlayingFlag.delete();
		}
		clearPlayList();
		latch.countDown();
		System.exit(0);
	}

	public void setCurFlag(Video v) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(curPlayingFlag));
			oos.writeObject(v);
			oos.close();
		} catch (Exception e) {
			errorHandler(121, e.getMessage(), "setCurFlag", "Operations", true);
		}
	}

	public int frame1() {
		try {
			JFrame frame = new JFrame("VLCMod - Resume or Start Over?");
			frame.setResizable(false);
			frame.setAlwaysOnTop(true);
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.setVisible(false);

			JLabel label1 = new JLabel("Do you whish to resume from where you have left last time or to Start Over?");

			JButton resume = new JButton("Resume");
			resume.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					ret = JOptionPane.YES_OPTION;
					latch2.countDown();
				}
			});
			JButton start = new JButton("Start Over");
			start.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					ret = JOptionPane.NO_OPTION;
					latch2.countDown();
				}
			});

			JPanel p1 = new JPanel(new GridBagLayout());
			JPanel p2 = new JPanel(new GridBagLayout());

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(10, 10, 10, 10);

			gbc.gridx = 0;
			gbc.gridy = 0;
			p2.add(resume, gbc);

			gbc.gridx = 1;
			gbc.gridy = 0;
			p2.add(start, gbc);

			gbc.gridx = 0;
			gbc.gridy = 0;
			p1.add(label1, gbc);

			gbc.gridx = 0;
			gbc.gridy = 1;
			p1.add(p2, gbc);

			frame.add(p1, BorderLayout.CENTER);
			frame.getRootPane().setDefaultButton(resume);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			latch2.await();
			frame.dispose();
		} catch (Exception e) {
			errorHandler(130, e.getMessage(), "frame1", "Operations", true);
		}
		return ret;
	}

	public int frame2() {
		try {
			JFrame frame = new JFrame("VLCMod - Playing File Changed.!");
			frame.setResizable(false);
			frame.setAlwaysOnTop(true);
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.setVisible(false);

			JLabel label1 = new JLabel("VLC Playing File Changed Unexcpectedly.!");
			JLabel label2 = new JLabel(
					"VLCMod failed to get Properties of that file.\n Hence VLCMod can no longer Monitor the Plat.");
			JLabel label3 = new JLabel(
					"I suggest you to close VLC and Open it again from the Folder or You can Play without Monitoring..");
			JLabel label4 = new JLabel(
					"If you click on \"Close VLC\", Both VLC and VLCMod will be closed.! or If you click on \"Play without Monitor\" only VLCMod will close.!");
			JLabel label5 = new JLabel("\n\tMake your Choice:");

			JButton close = new JButton("Close VLC");
			close.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					frame.setAlwaysOnTop(false);
					int res = JOptionPane.showConfirmDialog(null, "Are you sure to close both VLC and VLCMod?",
							"Confirm Action", JOptionPane.YES_NO_OPTION);
					frame.setAlwaysOnTop(true);
					if (res == JOptionPane.YES_OPTION) {
						ret = JOptionPane.YES_OPTION;
						latch3.countDown();
					}
				}
			});

			JButton play = new JButton("Play without Monitor.");
			play.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					frame.setAlwaysOnTop(false);
					int res = JOptionPane.showConfirmDialog(null, "By Proceeding, VLCMod will stop Monitoring and Exit",
							"Confirmation Action", JOptionPane.YES_NO_OPTION);
					frame.setAlwaysOnTop(true);
					if (res == JOptionPane.YES_OPTION)
						ret = JOptionPane.YES_OPTION;
					latch3.countDown();
				}
			});

			JPanel p1 = new JPanel(new GridBagLayout());
			JPanel p2 = new JPanel(new GridBagLayout());
			JPanel p3 = new JPanel(new GridBagLayout());

			GridBagConstraints gbc1 = new GridBagConstraints();
			gbc1.insets = new Insets(3, 3, 3, 3);

			gbc1.gridx = 0;
			gbc1.gridy = 0;
			p1.add(label1, gbc1);

			gbc1.gridx = 0;
			gbc1.gridy = 1;
			p1.add(label2, gbc1);

			gbc1.gridx = 0;
			gbc1.gridy = 2;
			p1.add(label3, gbc1);

			gbc1.gridx = 0;
			gbc1.gridy = 3;
			p1.add(label4, gbc1);

			gbc1.gridx = 0;
			gbc1.gridy = 4;
			p1.add(label5, gbc1);

			GridBagConstraints gbc2 = new GridBagConstraints();
			gbc2.insets = new Insets(10, 10, 10, 10);

			gbc2.gridx = 0;
			gbc2.gridy = 0;
			p2.add(close, gbc2);

			gbc2.gridx = 1;
			gbc2.gridy = 0;
			p2.add(play, gbc2);

			gbc2.gridx = 0;
			gbc2.gridy = 0;
			p3.add(p1, gbc2);

			gbc2.gridx = 0;
			gbc2.gridy = 1;
			p3.add(p2, gbc2);

			frame.add(p3, BorderLayout.CENTER);
			frame.getRootPane().setDefaultButton(close);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			latch3.await();
			frame.dispose();
		} catch (Exception e) {
			errorHandler(131, e.getMessage(), "frame2", "Operations", true);
		}
		return ret;
	}

	public void fileChangeHandler(String File) {
		File newFile = new File(File);
		if (!newFile.exists()) {
			int res = frame2();
			if (res == JOptionPane.YES_OPTION) {
				c.quit();
				onClose();
			} else if (res == JOptionPane.NO_OPTION) {
				onClose();
			}
		} else if ((newFile.getParent().equals(null))) {
			int res = frame2();
			if (res == JOptionPane.YES_OPTION) {
				c.quit();
				onClose();
			} else if (res == JOptionPane.NO_OPTION) {
				onClose();
			}
		} else {
			String Par = newFile.getParent();
			File ParDir = new File(Par);
			if (ParDir.exists()) {
				return;
			} else {
				int res = frame2();
				if (res == JOptionPane.YES_OPTION) {
					c.quit();
					onClose();
				} else if (res == JOptionPane.NO_OPTION) {
					onClose();
				}
			}
		}
	}

	public void errorFrame(Component c, String Msg, String Title, int Type) {
		CountDownLatch errorLatch = new CountDownLatch(1);

		JFrame frame = new JFrame(Title);
		frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);

		ImageIcon icon = new ImageIcon(this.getClass().getResource("/imag.JPG"));
		JLabel label = new JLabel(Msg);
		label.setIcon(icon);

		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
				errorLatch.countDown();
			}
		});

		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);

		gbc.gridx = 0;
		gbc.gridy = 0;
		p.add(label, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		p.add(ok, gbc);

		frame.add(p, BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		try {
			errorLatch.await();
		} catch (InterruptedException e) {
			errorHandler(133, e.getMessage(), "ErrorFrame", "Operations", false);
		}
	}
}
