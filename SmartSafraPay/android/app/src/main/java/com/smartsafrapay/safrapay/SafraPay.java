package com.smartsafrapay.safrapay;

import static java.lang.Math.ceil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.util.HashMap;
import java.util.Map;

import br.com.setis.safra.integracaosafra.Gerenciador;
import br.com.setis.safra.integracaosafra.PrinterIntegracao;
import br.com.setis.safra.integracaosafra.entidades.DadosAutomacao;
import br.com.setis.safra.integracaosafra.entidades.EntradaTransacao;
import br.com.setis.safra.integracaosafra.entidades.Operacoes;
import br.com.setis.safra.integracaosafra.entidades.RequestApi;
import br.com.setis.safra.integracaosafra.entidades.SaidaTransacao;
import br.com.setis.safra.integracaosafra.listeners.PrinterListener;
import br.com.setis.safra.integracaosafra.listeners.TransacaoListenerCallback;
import br.com.setis.safra.integracaosafra.printer.PrinterStatus;
import br.com.setis.safra.integracaosafra.printer.PrinterTextAlignment;
import br.com.setis.safra.integracaosafra.util.ReturnCodes;

public class SafraPay extends ReactContextBaseJavaModule {

    private static final int WIDTH = 400;
    private Gerenciador gerenciadorIntegracaoSafra;
    private Promise mPaymentPromise;
    private final TransacaoListenerCallback callbackTransacaoIntegracao = new TransacaoListenerCallback() {

        @Override
        public void transacaoFinalizada(SaidaTransacao saidaTransacao) {
            if (saidaTransacao.obtemResultadoTransacao() == ReturnCodes.RESULT_APROVADA) {
                final WritableMap map = Arguments.createMap();
                map.putString("retCode", saidaTransacao.obtemCodigoRespostaTransacao());
                map.putString("transactionCode", saidaTransacao.obtemNumeroEc());
                map.putString("transactionId", saidaTransacao.obtemDoc());
                map.putString("message", "Aprovado");
                map.putString("card", saidaTransacao.obtemCartaoMascarado());
                map.putString("nsu", saidaTransacao.obtemNsuTransacao());
                mPaymentPromise.resolve(map);
            } else if (saidaTransacao.obtemResultadoTransacao() == ReturnCodes.RESULT_ESTORNADA) {
                mPaymentPromise.reject(null, "Reembolso Concluido");
            } else if (saidaTransacao.obtemOperacaoRealizada() == Operacoes.CANCELAMENTO) {
                mPaymentPromise.reject(null, "Pagamento Cancelado");
            } else if (saidaTransacao.obtemOperacaoRealizada() == Operacoes.CANCELAMENTO_VIA_NSU) {
                mPaymentPromise.reject(null, "Pagamento Cancelado via NSU");
            } else if (saidaTransacao.obtemResultadoTransacao() == ReturnCodes.POS_CANCEL) {
                mPaymentPromise.reject(null, "Pagamento Cancelado pelo operador");
            }

        }

        @Override
        public void transacaoException(Exception e) {
            mPaymentPromise.reject(null, e.getMessage());
        }
    };


    //constructor
    public SafraPay(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @NonNull
    @Override
    public String getName() {
        return "SafraPay";
    }

    public Map<String, Object> getConstants() {
        Map<String, Object> constants = new HashMap<>();
        constants.put("CANCELAMENTO", Operacoes.CANCELAMENTO.getValue());
        constants.put("CANCELAMENTO_VIA_NSU", Operacoes.CANCELAMENTO_VIA_NSU.getValue());
        constants.put("DEVOLUCAO_PIX", Operacoes.DEVOLUCAO_PIX.getValue());
        constants.put("NONE", Operacoes.NONE.getValue());
        constants.put("OUTROS", Operacoes.OUTROS.getValue());
        constants.put("PRE_AUTORIZACAO", Operacoes.PRE_AUTORIZACAO.getValue());
        constants.put("PRE_AUTORIZACAO_CANCEL", Operacoes.PRE_AUTORIZACAO_CANCEL.getValue());
        constants.put("PRE_AUTORIZACAO_CONFIRM", Operacoes.PRE_AUTORIZACAO_CONFIRM.getValue());
        constants.put("PRE_AUTORIZACAO_SOLICITA", Operacoes.PRE_AUTORIZACAO_SOLICITA.getValue());
        constants.put("REIMPRESSAO", Operacoes.REIMPRESSAO.getValue());
        constants.put("VENDA_CREDITO", Operacoes.VENDA_CREDITO.getValue());
        constants.put("VENDA_CREDITO_A_VISTA", Operacoes.VENDA_CREDITO_A_VISTA.getValue());
        constants.put("VENDA_CREDITO_PARC_COM_JUROS", Operacoes.VENDA_CREDITO_PARC_COM_JUROS.getValue());
        constants.put("VENDA_CREDITO_PARC_SEM_JUROS", Operacoes.VENDA_CREDITO_PARC_SEM_JUROS.getValue());
        constants.put("VENDA_DEBITO", Operacoes.VENDA_DEBITO.getValue());
        constants.put("VENDA_DEBITO_A_VISTA", Operacoes.VENDA_DEBITO_A_VISTA.getValue());
        constants.put("VENDA_DEBITO_FATURA", Operacoes.VENDA_DEBITO_FATURA.getValue());
        constants.put("VENDA_QRCODE", Operacoes.VENDA_QRCODE.getValue());
        constants.put("VENDA_VOUCHER", Operacoes.VENDA_VOUCHER.getValue());
        constants.put("RESULT_APROVADA", ReturnCodes.RESULT_APROVADA);
        constants.put("RESULT_OK", ReturnCodes.RESULT_OK);
        constants.put("CANCELAMENTO", Operacoes.CANCELAMENTO.getValue());
        constants.put("CANCELAMENTO_VIA_NSU", Operacoes.CANCELAMENTO_VIA_NSU.getValue());
        return constants;
    }

    @ReactMethod
    public void show(String text) {
        Context context = getReactApplicationContext();
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    @ReactMethod
    public void activate(String nameCompanyAutomation, String nameAutomation, String automationVersion, Promise promise) {
        Context context = getReactApplicationContext();
        try {
            DadosAutomacao dadosAutomacao = new DadosAutomacao(nameCompanyAutomation, nameAutomation, automationVersion);
            gerenciadorIntegracaoSafra = new Gerenciador(context, dadosAutomacao);
            promise.resolve("success");
        } catch (Exception e) {
            promise.reject("fail", e.getLocalizedMessage());
        }

    }

    @ReactMethod
    public void payment(int valuetransaction, int type, String identifierTransacaoAutomacao, Promise promise) {
        EntradaTransacao entradaTransacao = new EntradaTransacao(valuetransaction, Operacoes.valueOf(type), identifierTransacaoAutomacao);
        RequestApi requestApi = new RequestApi(entradaTransacao);
        gerenciadorIntegracaoSafra.realizaTransacao(requestApi, callbackTransacaoIntegracao);
        mPaymentPromise = promise;
    }

    @ReactMethod
    public void reversal(String automationVersion, Promise promise) {
        EntradaTransacao entradaTransacao = new EntradaTransacao(Operacoes.CANCELAMENTO, automationVersion);
        // EntradaTransacao entradaTransacao = new EntradaTransacao("020005179576", Operacoes.CANCELAMENTO_VIA_NSU, "01");
        RequestApi requestApi = new RequestApi(entradaTransacao);
        gerenciadorIntegracaoSafra.realizaTransacao(requestApi, callbackTransacaoIntegracao);
        mPaymentPromise = promise;
    }

    @ReactMethod
    public void reprint(String automationVersion, Promise promise) {
        EntradaTransacao entradaTransacao = new EntradaTransacao(Operacoes.REIMPRESSAO, automationVersion);
        RequestApi requestApi = new RequestApi(entradaTransacao);
        gerenciadorIntegracaoSafra.realizaTransacao(requestApi, callbackTransacaoIntegracao);
        mPaymentPromise = promise;
    }

    @ReactMethod
    public void print(String imgBase64, Promise promise) {
        final PrinterIntegracao printerIntegracao = gerenciadorIntegracaoSafra.getPrinterIntegracao();
        Bitmap bmp;
        byte[] imageByte;
        imageByte = Base64.decode(imgBase64, Base64.DEFAULT);
        bmp = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        int tmpHeight = (int) ceil((WIDTH * bmp.getHeight()) / bmp.getWidth());
        printerIntegracao.addBitmap(Bitmap.createScaledBitmap(bmp, WIDTH, tmpHeight, true), PrinterTextAlignment.CENTER);

        printerIntegracao.print(new PrinterListener() {
            @Override
            public void eventoSaida(PrinterStatus printerStatus) {
                if (printerStatus == PrinterStatus.ERROR_NO_PAPER) {
                    promise.reject(null, "Impressora sem papel");
                }
            }

            @Override
            public void eventoException(Exception e) {
                promise.reject(null, e.getMessage());
            }
        }, 50);
    }

}
