package com.ofss.daytrader.portfolios.repository;

import com.ofss.daytrader.entities.AccountProfileDataBean;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AccountsProfileRepository extends CrudRepository<AccountProfileDataBean, String>{

	@SuppressWarnings("unchecked")
	public AccountProfileDataBean save(AccountProfileDataBean profileData);
	
	//@Query(value="select * from accountprofile ap where ap.userid =?1", nativeQuery=true)
	public AccountProfileDataBean findAccountProfileDataByuserID(String userId);
	
    @Modifying      // to mark delete or update query
    @Query(value = "delete from accountprofile where userid like 'ru:%'", nativeQuery = true)
    public void deleteAccountprofileByUser();
    
    @Transactional
	@Query(value="select passwd from accountprofile where userID = ?1", nativeQuery=true)
	public String getPwdByUserid(String userId);
}
