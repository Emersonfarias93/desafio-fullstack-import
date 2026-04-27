package br.com.esdevcode.backend.service;

import br.com.esdevcode.backend.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class CsvLeadParser {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);
    private static final DateTimeFormatter FORMATO_BR_COMPLETO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_BR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public List<CsvLeadRow> parse(Path caminhoArquivo) {
        List<CsvLeadRow> linhas = new ArrayList<>();

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                Files.newInputStream(caminhoArquivo),
                                StandardCharsets.UTF_8.newDecoder()
                                        .onMalformedInput(CodingErrorAction.REPORT)
                                        .onUnmappableCharacter(CodingErrorAction.REPORT)
                        )
                )
        ) {
            reader.readLine();

            String linha;
            int linhaCsv = 1;
            while ((linha = reader.readLine()) != null) {
                linhaCsv++;

                if (linha.isBlank()) {
                    linhas.add(new CsvLeadRow(linhaCsv, "", "", "", "", null, "Linha em branco."));
                    continue;
                }

                linhas.add(parseLinha(linhaCsv, linha));
            }

            return linhas;
        } catch (BusinessException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new BusinessException("Nao foi possivel ler o arquivo CSV em UTF-8.");
        }
    }

    private CsvLeadRow parseLinha(int linhaCsv, String linha) {
        List<String> colunas = dividirCsv(linha);

        if (colunas.size() != 5) {
            return new CsvLeadRow(linhaCsv, "", "", "", "", null, "Quantidade de colunas invalida.");
        }

        String nome = limpar(colunas.get(0));
        String email = limpar(colunas.get(1)).toLowerCase();
        String telefone = limpar(colunas.get(2));
        String origem = limpar(colunas.get(3));
        String dataCadastroTexto = limpar(colunas.get(4));

        List<String> erros = new ArrayList<>();

        if (nome.isBlank()) {
            erros.add("Nome obrigatorio");
        }

        if (email.isBlank()) {
            erros.add("Email obrigatorio");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            erros.add("Email invalido");
        }

        LocalDateTime dataCadastro = parseData(dataCadastroTexto);
        if (dataCadastro == null) {
            erros.add("Data de cadastro invalida");
        }

        return new CsvLeadRow(
                linhaCsv,
                nome,
                email,
                telefone,
                origem,
                dataCadastro,
                erros.isEmpty() ? null : String.join("; ", erros)
        );
    }

    private List<String> dividirCsv(String linha) {
        List<String> colunas = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        boolean entreAspas = false;

        for (int i = 0; i < linha.length(); i++) {
            char caractere = linha.charAt(i);

            if (caractere == '"') {
                if (entreAspas && i + 1 < linha.length() && linha.charAt(i + 1) == '"') {
                    atual.append('"');
                    i++;
                } else {
                    entreAspas = !entreAspas;
                }
                continue;
            }

            if (caractere == ',' && !entreAspas) {
                colunas.add(atual.toString());
                atual.setLength(0);
                continue;
            }

            atual.append(caractere);
        }

        if (entreAspas) {
            throw new BusinessException("CSV invalido. Ha aspas sem fechamento.");
        }

        colunas.add(atual.toString());
        return colunas;
    }

    private String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private LocalDateTime parseData(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        List<DateParser> parsers = List.of(
                texto -> LocalDateTime.parse(texto, FORMATO_BR_COMPLETO),
                texto -> LocalDate.parse(texto, FORMATO_BR_DATA).atStartOfDay(),
                LocalDateTime::parse,
                texto -> LocalDate.parse(texto).atStartOfDay()
        );

        for (DateParser parser : parsers) {
            try {
                return parser.parse(valor);
            } catch (DateTimeParseException ignored) {
            }
        }

        return null;
    }

    @FunctionalInterface
    private interface DateParser {
        LocalDateTime parse(String valor);
    }
}
