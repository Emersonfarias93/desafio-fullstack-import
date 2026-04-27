package br.com.esdevcode.backend.service;

import br.com.esdevcode.backend.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
public class CsvFileValidator {

    private static final long TAMANHO_MAXIMO_BYTES = 100 * 1024 * 1024;

    private static final List<String> CABECALHO_ESPERADO = List.of(
            "nome",
            "email",
            "telefone",
            "origem",
            "data_cadastro"
    );

    private static final List<String> CONTENT_TYPES_PERMITIDOS = List.of(
            "text/csv",
            "application/csv",
            "application/vnd.ms-excel",
            "text/plain",
            "application/octet-stream"
    );

    public void validar(MultipartFile arquivo) {
        validarObrigatorio(arquivo);
        validarNome(arquivo);
        validarTamanho(arquivo);
        validarContentType(arquivo);
        validarEncodingECabecalho(arquivo);
    }

    private void validarObrigatorio(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessException("Arquivo CSV e obrigatorio.");
        }
    }

    private void validarNome(MultipartFile arquivo) {
        String nomeArquivo = arquivo.getOriginalFilename();

        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            throw new BusinessException("Nome do arquivo e obrigatorio.");
        }

        if (!nomeArquivo.toLowerCase().endsWith(".csv")) {
            throw new BusinessException("O arquivo precisa estar no formato CSV.");
        }
    }

    private void validarTamanho(MultipartFile arquivo) {
        if (arquivo.getSize() > TAMANHO_MAXIMO_BYTES) {
            throw new BusinessException("Arquivo muito grande. O tamanho maximo permitido e 100MB.");
        }
    }

    private void validarContentType(MultipartFile arquivo) {
        String contentType = arquivo.getContentType();

        if (contentType == null || contentType.isBlank()) {
            return;
        }

        if (!CONTENT_TYPES_PERMITIDOS.contains(contentType)) {
            throw new BusinessException("Tipo de arquivo invalido. Envie um arquivo CSV.");
        }
    }

    private void validarEncodingECabecalho(MultipartFile arquivo) {
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                arquivo.getInputStream(),
                                StandardCharsets.UTF_8.newDecoder()
                                        .onMalformedInput(CodingErrorAction.REPORT)
                                        .onUnmappableCharacter(CodingErrorAction.REPORT)
                        )
                )
        ) {
            String cabecalho = reader.readLine();

            if (cabecalho == null || cabecalho.isBlank()) {
                throw new BusinessException("CSV vazio ou sem cabecalho.");
            }

            validarCabecalho(removerBomUtf8(cabecalho));

        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException("Nao foi possivel validar o arquivo CSV. Verifique se esta em UTF-8.");
        }
    }

    private void validarCabecalho(String cabecalho) {
        List<String> colunas = Arrays.stream(cabecalho.split(",", -1))
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();

        if (!CABECALHO_ESPERADO.equals(colunas)) {
            throw new BusinessException(
                    "CSV invalido. Cabecalho esperado: nome,email,telefone,origem,data_cadastro"
            );
        }
    }

    private String removerBomUtf8(String linha) {
        if (linha != null && linha.startsWith("\uFEFF")) {
            return linha.substring(1);
        }

        return linha;
    }
}
