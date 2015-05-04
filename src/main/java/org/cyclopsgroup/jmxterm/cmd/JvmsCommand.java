package org.cyclopsgroup.jmxterm.cmd;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.JMException;

import org.cyclopsgroup.jcli.annotation.Cli;
import org.cyclopsgroup.jcli.annotation.Option;
import org.cyclopsgroup.jmxterm.Command;
import org.cyclopsgroup.jmxterm.JavaProcess;
import org.cyclopsgroup.jmxterm.Session;
import org.cyclopsgroup.jmxterm.SyntaxUtils;

/**
 * Command to list all running local JVM processes
 * 
 * @author <a href="mailto:jiaqi.guo@gmail.com">Jiaqi Guo</a>
 */
@Cli( name = "jvms", description = "List all running local JVM processes" )
public class JvmsCommand
    extends Command
{
    private boolean pidOnly;
    
    public static Map<String, String> getJvmsList(Session session, String filter) {
        List<JavaProcess> processList;
        Map<String, String> sessions = new HashMap<String, String>();

        // classworlds has some hard coded stdout printing. Therefore stdout needs to be redirected temporarily to avoid
        // meaningless console output
        PrintStream stdOut = System.out;
        System.setOut( SyntaxUtils.NULL_PRINT_STREAM );
        try
        {
            processList = session.getProcessManager().list();
        }
        finally
        {
            System.setOut( stdOut );
        }
        for ( JavaProcess p : processList ) {
            if (filter == null || filter.equalsIgnoreCase(p.getDisplayName())) {
                sessions.put(
                    String.valueOf(p.getProcessId()),
                    String
                        .format("%-8d (%s) - %s", p.getProcessId(), p.isManageable() ? "m" : " ", p.getDisplayName()));
            }
        }
        return sessions;
    } 

    /**
     * @inheritDoc
     */
    @Override
    public void execute()
        throws IOException, JMException
    {
        Session session = getSession();
        Map<String, String> sessions = getJvmsList(session, null);
        for ( Map.Entry<String, String> entry : sessions.entrySet()  )
        {
            if ( pidOnly )
            {
                session.output.println( String.valueOf( entry.getKey() ) );
            }
            else
            {

                session.output.println( String.format( entry.getValue() ) );
            }
        }
    }

    /**
     * @param pidOnly Flag to notify command to only print out PID instead of more details
     */
    @Option( name = "p", longName = "pidonly", description = "Only print out PID" )
    public final void setPidOnly( boolean pidOnly )
    {
        this.pidOnly = pidOnly;
    }
}
