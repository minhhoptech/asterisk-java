package org.asteriskjava.live.internal;

import java.util.Date;

import org.asteriskjava.live.ManagerCommunicationException;
import org.asteriskjava.live.MeetMeUser;
import org.asteriskjava.live.MeetMeUserState;
import org.asteriskjava.manager.action.CommandAction;

class MeetMeUserImpl extends AbstractLiveObject implements MeetMeUser
{
    private static final String COMMAND_PREFIX = "meetme";
    private static final String MUTE_COMMAND = "mute";
    private static final String UNMUTE_COMMAND = "unmute";
    private static final String KICK_COMMAND = "kick";

    private final ManagerConnectionPool connectionPool;
    
    private final MeetMeRoomImpl room;
    private final Integer userNumber;
    private final AsteriskChannelImpl channel;
    private final Date dateJoined;

    private Date dateLeft;
    private MeetMeUserState state;
    private boolean talking;
    private boolean muted;

    MeetMeUserImpl(ManagerConnectionPool connectionPool, MeetMeRoomImpl room, Integer userNumber,
            AsteriskChannelImpl channel, Date dateJoined)
    {
        super();
        this.connectionPool = connectionPool;
        this.room = room;
        this.userNumber = userNumber;
        this.channel = channel;
        this.dateJoined = dateJoined;
        this.state = MeetMeUserState.JOINED;
    }

    public MeetMeRoomImpl getRoom()
    {
        return room;
    }

    public AsteriskChannelImpl getChannel()
    {
        return channel;
    }

    public Date getDateJoined()
    {
        return dateJoined;
    }

    public Date getDateLeft()
    {
        return dateLeft;
    }

    /**
     * Sets the status to {@link MeetMeUserState#LEFT} and dateLeft to the given date.
     * 
     * @param dateLeft the date this user left the room.
     */
    void left(Date dateLeft)
    {
        MeetMeUserState oldState;
        synchronized (this)
        {
            oldState = this.state;
            this.dateLeft = dateLeft;
            this.state = MeetMeUserState.LEFT;
        }
        firePropertyChange("state", oldState, state);
    }

    public MeetMeUserState getState()
    {
        return state;
    }

    public boolean isTalking()
    {
        return talking;
    }

    void setTalking(boolean talking)
    {
        boolean oldTalking = this.talking;
        this.talking = talking;
        firePropertyChange("talking", oldTalking, talking);
    }

    public boolean isMuted()
    {
        return muted;
    }

    void setMuted(boolean muted)
    {
        boolean oldMuted = this.muted;
        this.muted = muted;
        firePropertyChange("muted", oldMuted, muted);
    }

    Integer getUserNumber()
    {
        return userNumber;
    }

    // action methods

    public void kick() throws ManagerCommunicationException
    {
        sendMeetMeUserCommand(KICK_COMMAND);
    }

    public void mute() throws ManagerCommunicationException
    {
        sendMeetMeUserCommand(MUTE_COMMAND);
    }

    public void unmute() throws ManagerCommunicationException
    {
        sendMeetMeUserCommand(UNMUTE_COMMAND);
    }

    private void sendMeetMeUserCommand(String command) throws ManagerCommunicationException
    {
        StringBuffer sb = new StringBuffer();
        sb.append(COMMAND_PREFIX);
        sb.append(" ");
        sb.append(command);
        sb.append(" ");
        sb.append(room.getRoomNumber());
        sb.append(" ");
        sb.append(userNumber);

        connectionPool.sendAction(new CommandAction(sb.toString()));
    }
    
    public String toString()
    {
        StringBuffer sb;
        int systemHashcode;

        sb = new StringBuffer("MeetMeUser[");

        synchronized (this)
        {
            sb.append("dateJoined='" + getDateJoined() + "',");
            sb.append("dateLeft='" + getDateLeft() + "',");
            sb.append("talking=" + isTalking() + ",");
            sb.append("muted=" + isMuted() + ",");
            sb.append("room=" + room + ",");
            systemHashcode = System.identityHashCode(this);
        }
        sb.append("systemHashcode=" + systemHashcode);
        sb.append("]");

        return sb.toString();
    }
}
