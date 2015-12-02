package nl.xillio.xill.plugins.document.services.xill;

import com.google.inject.Singleton;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.document.data.MetaExpressionUDMMapper;
import nl.xillio.xill.plugins.document.data.UDMDocument;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is responsible for creating {@link nl.xillio.xill.plugins.document.data.UDMDocument} from
 * a {@link nl.xillio.xill.api.components.MetaExpression}.
 *
 * @author Thomas Biesaart
 */
@Singleton
public class UDMDocumentFactory implements UDMDocumentConstructor, UDMDocumentBuilder {


    @Override
    public UDMDocument build(MetaExpression expression) {
        return new MetaExpressionUDMMapper(expression);
    }

    @Override
    public MetaExpression buildStructure(String contentType, MetaExpression currentVersion, MetaExpression versions) {
        LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();
        result.put("contenttype", ExpressionBuilder.fromValue(contentType));
        result.put("source", buildHistory(currentVersion, versions));
        result.put("target", buildHistory(currentVersion, versions));

        return ExpressionBuilder.fromValue(result);
    }

    private MetaExpression buildHistory(MetaExpression currentVersion, MetaExpression versions) {
        LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();
        result.put("current", buildRevision(currentVersion));
        result.put("versions", buildRevisionList(versions));

        return ExpressionBuilder.fromValue(result);
    }

    private MetaExpression buildRevisionList(MetaExpression bodies) {
        if (bodies.getType() != ExpressionDataType.LIST) {
            throw new IllegalArgumentException("Invalid Structure: Document history must be a list of bodies");
        }

        @SuppressWarnings("unchecked")
        List<MetaExpression> children = (List<MetaExpression>) bodies.getValue();

        return ExpressionBuilder.fromValue(
                children.stream().map(this::buildRevision).collect(Collectors.toList())
        );
    }

    @SuppressWarnings("unchecked")
    private MetaExpression buildRevision(MetaExpression body) {
        LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();

        if (body.getType() != ExpressionDataType.OBJECT) {
            throw new IllegalArgumentException("Unable to parse as a document body [" + body + "]");
        }

        ((Map<String, MetaExpression>) body.getValue()).entrySet().forEach(
                entry -> {
                    if ("version".equals(entry.getKey())) {
                        result.put("version", entry.getValue());
                    } else {
                        result.put(entry.getKey(), buildDecorator(entry.getValue()));
                    }
                }

        );


        return ExpressionBuilder.fromValue(result);
    }

    @SuppressWarnings("unchecked")
    private MetaExpression buildDecorator(MetaExpression body) {
        LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();

        if (body.getType() != ExpressionDataType.OBJECT) {
            throw new IllegalArgumentException("Unable to parse decorator [" + body + "]");
        }

        ((Map<String, MetaExpression>) body.getValue()).entrySet().forEach(
                entry -> result.put(entry.getKey(), parseField(entry.getValue()))
        );

        return ExpressionBuilder.fromValue(result);
    }

    private MetaExpression parseField(MetaExpression value) {
        // Create copy
        return MetaExpression.parseObject(MetaExpression.extractValue(value));
    }
}
