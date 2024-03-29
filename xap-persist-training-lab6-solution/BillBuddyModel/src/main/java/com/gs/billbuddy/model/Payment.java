package com.gs.billbuddy.model;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceIndex;
import com.gigaspaces.annotation.pojo.SpaceInitialLoadQuery;
import com.gigaspaces.annotation.pojo.SpaceRouting;
import com.gigaspaces.metadata.index.SpaceIndexType;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.openspaces.core.cluster.ClusterInfo;


/** 
* Payment class is a POJO which has transaction information between user and merchant 
* 
* @author 123Completed
*/

@Entity
@SuppressWarnings("serial")
@SpaceClass
public class Payment implements Serializable{
	@Id
    private String paymentId;
    private Integer payingAccountId;
    private Integer receivingMerchantId;
    private String description;
    private Double paymentAmount;
    private TransactionStatus status;
    private Date createdDate;
    
    
    @SpaceInitialLoadQuery
    public String initialLoadQuery(ClusterInfo clusterInfo) {
    	// System.out.println("MOD(receivingMerchantId,"+ clusterInfo.getNumberOfInstances() + ")=" + clusterInfo.getInstanceId());
        return "paymentAmount > 50 AND MOD(receivingMerchantId," + clusterInfo.getNumberOfInstances() + ")=" + (clusterInfo.getInstanceId()-1);
    	// return "paymentAmount > 50 AND receivingMerchantId%" + clusterInfo.getNumberOfInstances() + "=" + clusterInfo.getInstanceId();
    }
    
    public Payment(String paymentId) {
        this.paymentId = paymentId;
    }

    public Payment() {
    }

    @SpaceId(autoGenerate = true)
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    @SpaceIndex(type=SpaceIndexType.BASIC)
	public Integer getPayingAccountId() {
		return payingAccountId;
	}

	public void setPayingAccountId(Integer payingAccountId) {
		this.payingAccountId = payingAccountId;
	}
	
	@SpaceRouting
	@SpaceIndex(type=SpaceIndexType.BASIC)
	public Integer getReceivingMerchantId() {
		return receivingMerchantId;
	}

	public void setReceivingMerchantId(Integer receivingMerchantId) {
		this.receivingMerchantId = receivingMerchantId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}
	
	@SpaceIndex(type=SpaceIndexType.BASIC)
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setPaymentAmount(Double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public Double getPaymentAmount() {
		return paymentAmount;
	}
	
}
