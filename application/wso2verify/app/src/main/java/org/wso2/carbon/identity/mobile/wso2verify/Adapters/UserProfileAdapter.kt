/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.identity.mobile.wso2verify.Adapters

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.user_profile_single.view.*
import org.wso2.carbon.identity.mobile.wso2verify.DatabaseHelper
import org.wso2.carbon.identity.mobile.wso2verify.Model.BiometricAuthProfile
import org.wso2.carbon.identity.mobile.wso2verify.R

class UserProfileAdapter(private val profiles: List<BiometricAuthProfile>, private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<UserProfileAdapter.UserProfileHolder>() {
    internal lateinit var db: DatabaseHelper
    class UserProfileHolder(itemView: View, val onClickListener: OnClickListener) : RecyclerView.ViewHolder(itemView) {
        val txt_id: TextView = itemView.txt_id
        val txt_username: TextView = itemView.txt_username
        val txt_tenant_domain: TextView = itemView.txt_tenant_domain
        val txt_user_store: TextView = itemView.txt_user_store
        var remove_btn: Button = itemView.btn_remove_profile

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserProfileHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.user_profile_single,
            parent, false
        )
        return UserProfileHolder(itemView, onClickListener)
    }

    override fun getItemCount() = profiles.size


    override fun onBindViewHolder(holder: UserProfileHolder, position: Int) {
        val currentItem = profiles[position]
        holder.txt_id.setText(SpannableStringBuilder()
            .bold { append("Device ID     : ") }.append(currentItem.deviceId)
        )
        holder.txt_username.setText(SpannableStringBuilder()
            .bold { append("Username      : ") }.append(currentItem.username)
        )
        holder.txt_tenant_domain.setText(SpannableStringBuilder()
            .bold { append("Tenant Domain : ") }.append(currentItem.tenantDomain)
        )
        holder.txt_user_store.setText(SpannableStringBuilder()
            .bold { append("User Store    : ") }.append(currentItem.userStore)
        )
        holder.remove_btn.setOnClickListener{
            holder.onClickListener.onDeleteClickLIstner(position)
        }

    }
//    private fun removeProfile(deviceId: String?) {
//        db = DatabaseHelper(context)
//        db.removeProfile(deviceId)
//        Toast.makeText(context, "Profile Removed", Toast.LENGTH_LONG).show()
//    }

    interface OnClickListener{
        fun onDeleteClickLIstner(position: Int)
    }
}
