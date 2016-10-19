package in.vasista.changePassword;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import in.vasista.milkosoft.mdkmf.R;
import in.vasista.vsales.sync.xmlrpc.XMLRPCApacheAdapter;
import in.vasista.vsales.sync.xmlrpc.XMLRPCMethodCallback;

/**
 * Created by Bekkam on 9/5/16.
 */
public class ChangePassword extends DialogFragment implements View.OnClickListener {

    Dialog dialog;
    ProgressBar changeStatusBar;
    EditText presentPass, newPass, verifyNewPass;
    CheckBox showNewPass, showVerifyPass;
    Button cancel, conform;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.change_password_layout, null);

        changeStatusBar = (ProgressBar) view.findViewById(R.id.password_change_progress);

        presentPass = (EditText) view.findViewById(R.id.old_pass);
        newPass = (EditText) view.findViewById(R.id.new_pass);
        verifyNewPass = (EditText) view.findViewById(R.id.verify_new_pass);

        showNewPass = (CheckBox) view.findViewById(R.id.new_pass_show);
        showVerifyPass = (CheckBox) view.findViewById(R.id.new_verify_pass_show);


        showNewPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    newPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    newPass.setSelection(newPass.getText().length());
                } else {
                    newPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    newPass.setSelection(newPass.getText().length());
                }
            }
        });
        showVerifyPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    verifyNewPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    verifyNewPass.setSelection(verifyNewPass.getText().length());
                } else {
                    verifyNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    verifyNewPass.setSelection(verifyNewPass.getText().length());
                }
            }
        });

        cancel = (Button) view.findViewById(R.id.cancel_password_change);
        conform = (Button) view.findViewById(R.id.proceed_password_change);

        cancel.setOnClickListener(this);
        conform.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog = getDialog();
        dialog.setCanceledOnTouchOutside(false);

        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.cancel_password_change:
                dialog.dismiss();
                break;

            case R.id.proceed_password_change:
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String userName = prefs.getString("userName", "");

                if (presentPass.getText().toString().isEmpty()){

                    setErrorMsg("This field is required", presentPass);
                } else if (newPass.getText().toString().isEmpty()){
                    setErrorMsg("This field is required", newPass);

                } else if (verifyNewPass.getText().toString().isEmpty()){
                    setErrorMsg("This field is required", verifyNewPass);

                } else if (!newPass.getText().toString().trim().equals(verifyNewPass.getText().toString().trim())){
                    setErrorMsg("This field is required", verifyNewPass);

                } else {

                    changeStatusBar.setVisibility(View.VISIBLE);

                    Map paramMap = new HashMap();
                    paramMap.put("userName", userName);
                    paramMap.put("currentPassword", presentPass.getText().toString());
                    paramMap.put("newPassword", newPass.getText().toString());
                    paramMap.put("newPasswordVerify", verifyNewPass.getText().toString());

                    try {
                        XMLRPCApacheAdapter adapter = new XMLRPCApacheAdapter(getActivity());
                        adapter.call("updatePassword", paramMap, changeStatusBar, new XMLRPCMethodCallback() {
                            public void callFinished(Object result, ProgressBar progressBar) {
                                if (result != null) {
                                    System.out.println("updatePassword " + result);
                                    Map  updatePassResult = (Map)((Map)result).get("updatedUserLogin");

                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    SharedPreferences.Editor prefEditor = prefs.edit();
                                    prefEditor.putString("password", verifyNewPass.getText().toString());
                                    prefEditor.apply();

                                    System.out.println("changePassord " + prefs.getString("password", ""));
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), "Change Success" , Toast.LENGTH_LONG).show();

                                }
                                if (progressBar != null) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    progressBar.setIndeterminate(false);
                                }

                            }
                        });
                        /*if (result != null) {
                            System.out.println("updatePassword " + result);
                        }*/
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Change Faild " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                break;
        }
    }

    private void setErrorMsg(String msg, EditText et) {

        et.setError(msg);

    }
}
