# Software Bill of Materials

The CycloneDX Maven plugin generates a Software Bill of Materials (SBOM) listing
MailClena's dependencies and their available license information. Test-scoped
dependencies are excluded.

Download the SBOM generated for this site build:

- [CycloneDX XML](sbom/bom.xml)
- [CycloneDX JSON](sbom/bom.json)

Both formats are generated automatically during Maven's `package` and `pre-site`
phases. The original files are `target/bom.xml` and `target/bom.json`. The site
build copies them to `target/site/sbom/`, so the published downloads match the
site build without requiring a separate generation command.
