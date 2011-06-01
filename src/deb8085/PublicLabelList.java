package deb8085;

import java.util.Vector;

public class PublicLabelList extends Vector<PublicLabel8085> {

    /**
	 * 
	 */
    private static final long serialVersionUID = -3349868346842008086L;

    // ***************************************************************************************************
    // スRスススXスgスススNス^
    public PublicLabelList() {
    }

    // ***************************************************************************************************
    // スpスuスススbスNスススxスススススススXスgスノ追会ソス
    public void addPublicLabel(String name, int addr)
            throws PublicLabelListException {
        name = name.toUpperCase();

        if (existPublicLabel(name))
            throw new PublicLabelListException("Name " + name
                    + " is already defined.");
        else if (existPublicLabel(addr))
            throw new PublicLabelListException("Public label for address "
                    + util.hex4(addr) + " is already defined.");
        else
            addElement(new PublicLabel8085(name, addr));

    }

    // ***************************************************************************************************
    // スpスuスススbスNスススxスススススススXスgスススス尞
    public void delPublicLabel(String name) throws PublicLabelListException {
        name = name.toUpperCase();

        try {
            removeElementAt(getPublicLabelIndex(name));
        } catch (PublicLabelListException e) {
            throw e;
        }

    }

    // ***************************************************************************************************
    // スwス閧オスススススOスフパスuスススbスNスススxスススススススヤ目ゑソス
    public int getPublicLabelIndex(String name) throws PublicLabelListException {
        for (int i = 0; i < size(); i++) {
            PublicLabel8085 p = (PublicLabel8085) elementAt(i);
            if (p.name.equals(name))
                return i;
        }
        // スススツゑソスススネゑソススススススス
        throw new PublicLabelListException(name + " is not defined.");
    }

    // ***************************************************************************************************
    // スwス閧ウス黷ススススOスフパスuスススbスNスススxススススススス驍ゥスヌゑソススス
    public boolean existPublicLabel(String name) {
        for (int i = 0; i < size(); i++) {
            PublicLabel8085 p = (PublicLabel8085) elementAt(i);
            if (p.name.equals(name))
                return true;
        }

        return false;
    }

    // ***************************************************************************************************
    // スwス閧ウス黷ススAスhスススXスノ対会ソスススススpスuスススbスNスススxススススススス驍ゥスヌゑソススス
    public boolean existPublicLabel(int addr) {
        for (int i = 0; i < size(); i++) {
            PublicLabel8085 p = (PublicLabel8085) elementAt(i);
            if (p.addr == addr)
                return true;
        }

        return false;
    }

    // ***************************************************************************************************
    // index スヤ目のパスuスススbスNスススxススススヤゑソス
    public PublicLabel8085 getPublicLabelAt(int index) {
        return (PublicLabel8085) elementAt(index);
    }

    // ***************************************************************************************************
    // スwス閧ウス黷ススススxスススススノ対会ソスススススAスhスススXススヤゑソス
    public int toPublicLabelAddr(String name) {
        for (int i = 0; i < size(); i++)
            if (((PublicLabel8085) elementAt(i)).name.equals(name))
                return ((PublicLabel8085) elementAt(i)).addr;

        // スpスuスススbスNスススxスススススススツゑソスススネゑソスススススススA-1 ススヤゑソススiスGススス[スIスj
        // スススフ関撰ソススススgスススニゑソススヘ、スススOスス existPublicLabel( name )
        // スナ托ソススンゑソススmスFススストゑソススススラゑソススB
        return -1;
    }

    // ***************************************************************************************************
    // スwス閧ウス黷ススAスhスススXスノ対会ソスススス驛会ソスxススススススヤゑソス
    public String toPublicLabelName(int addr) {
        for (int i = 0; i < size(); i++)
            if (((PublicLabel8085) elementAt(i)).addr == addr)
                return ((PublicLabel8085) elementAt(i)).name;

        // スpスuスススbスNスススxスススススススツゑソスススネゑソスススススススAnull ススヤゑソススiスGススス[スIスj
        // スススフ関撰ソススススgスススニゑソススヘ、スススOスス existPublicLabel( name )
        // スナ托ソススンゑソススmスFススストゑソススススラゑソススB
        return null;
    }

    // ***************************************************************************************************
    // スwス閧ウス黷ススAスhスススXスノ対会ソスススス驛会ソスxススススヤゑソス
    public PublicLabel8085 toPublicLabel(int addr) {
        for (int i = 0; i < size(); i++)
            if (((PublicLabel8085) elementAt(i)).addr == addr)
                return ((PublicLabel8085) elementAt(i));

        // スpスuスススbスNスススxスススススススツゑソスススネゑソスススススススAnull ススヤゑソススiスGススス[スIスj
        // スススフ関撰ソススススgスススニゑソススヘ、スススOスス existPublicLabel( name )
        // スナ托ソススンゑソススmスFススストゑソススススラゑソススB
        return null;
    }

}