package com.gs;

import com.gigaspaces.client.ChangeModifiers;
import com.gigaspaces.client.ChangeSet;
import com.gigaspaces.client.TakeModifiers;
import com.gigaspaces.client.mutators.SpaceEntryMutator;
import com.gigaspaces.internal.client.QueryResultTypeInternal;
import com.gigaspaces.internal.metadata.EntryType;
import com.gigaspaces.internal.metadata.ITypeDesc;
import com.gigaspaces.internal.metadata.PojoIntrospector;
import com.gigaspaces.internal.server.space.SpaceUidFactory;
import com.gigaspaces.internal.server.storage.IEntryData;
import com.gigaspaces.internal.transport.EntryPacket;
import com.gigaspaces.internal.transport.EntryPacketFactory;
import com.gigaspaces.internal.transport.IEntryPacket;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.query.IdQuery;
import com.j_spaces.core.OperationID;
import com.mycompany.app.model.Person;
import com.mycompany.app.model.Product;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import org.openspaces.core.GigaSpace;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

public class SpaceReplay {
    GigaSpace gs;

    public SpaceReplay(GigaSpace gs) {
        this.gs = gs;
        //ToDo - Assuming type exists in target space otherwise register all types before start
        gs.getTypeManager().registerTypeDescriptor(Product.class);
        gs.getTypeManager().registerTypeDescriptor(Person.class);
    }


    public void write(String type, IEntryData entry, OperationID operationID, String uid){
        SpaceTypeDescriptor typeDescriptor = gs.getTypeManager().getTypeDescriptor(type);
        PojoIntrospector pojoIntrospector = new PojoIntrospector<>((ITypeDesc) typeDescriptor);
        Object target = pojoIntrospector.newInstance();
        pojoIntrospector.setValues(target, entry.getFixedPropertiesValues());
        pojoIntrospector.setUID(target, uid);
        gs.write(target);

    }

    public void remove(String type, String uid) throws UnusableEntryException, TransactionException, RemoteException, InterruptedException {
        gs.getSpace().getDirectProxy().takeByUid(uid, null, TakeModifiers.NONE.getCode(), QueryResultTypeInternal.OBJECT_JAVA, false);
    }

    public void change(String type, String uid, Collection<SpaceEntryMutator> mutators) throws TransactionException, RemoteException {
        ChangeSet changeSet = new ChangeSet();
        SpaceTypeDescriptor typeDescriptor = gs.getTypeManager().getTypeDescriptor(type);
        PojoIntrospector pojoIntrospector = new PojoIntrospector<>((ITypeDesc) typeDescriptor);
        Object target = pojoIntrospector.newInstance();
        pojoIntrospector.setUID(target, uid);
        Iterator<SpaceEntryMutator> iterator = mutators.iterator();
        while (iterator.hasNext()) {
            SpaceEntryMutator mutator = iterator.next();
            changeSet.custom(mutator);
        }

        String id = SpaceUidFactory.getIdStringFromUID(SpaceUidFactory.generateTypePrefix(type), uid);
        System.out.println("About to change object type: "+ type + " uid: " +uid +"id: "+ id);
        //ToDo check type of id in type descriptor and cast accordingly, assumimg here Integer id
        IdQuery<Object> idQuery = new IdQuery(pojoIntrospector.getType(),Integer.parseInt(id));
        gs.change(idQuery, changeSet);
    }
}
