package fr.devmaster.bossbar;

public class BossBarException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BossBarException() {
		BossBarMain.Debug("An exception occured");
	}
	
	public BossBarException(String exception){
		BossBarMain.Debug(exception);
	}

}
