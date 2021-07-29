package org.tbk.lightning.lnurl.example.domain;

import fr.acinq.bitcoin.ByteVector64;
import fr.acinq.bitcoin.Crypto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jmolecules.ddd.annotation.Service;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.tbk.lightning.lnurl.example.lnurl.K1Manager;
import org.tbk.lnurl.K1;
import scodec.bits.ByteVector;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
class WalletUserServiceImpl implements WalletUserService {

    @NonNull
    private final WalletUsers users;

    @NonNull
    private final LinkingKeys linkingKeys;

    @NonNull
    private final K1Manager k1Manager;

    @TransactionalEventListener
    void on(WalletUser.WalletUserCreatedEvent event) {
        WalletUser domain = users.findById(event.getDomainId())
                .orElseThrow(() -> new EmptyResultDataAccessException("WalletUser key not found", 1));

        log.info("Received application event: {}", domain);
    }

    @Override
    @Transactional
    public WalletUser login(byte[] linkingKey, byte[] signature, K1 k1) {
        boolean validK1 = k1Manager.isValid(k1);
        if (!validK1) {
            throw new IllegalArgumentException("k1 value has either expired or was not generated by this service.");
        }

        boolean loginVerified = verifyLogin(linkingKey, signature, k1);
        if (!loginVerified) {
            throw new IllegalArgumentException("k1 and signature could not be verified.");
        }

        k1Manager.invalidate(k1);

        WalletUser user = getOrCreateWalletUser(linkingKey);
        return users.save(user.login());
    }

    private WalletUser getOrCreateWalletUser(byte[] linkingKey) {
        Optional<LinkingKey> linkingKeyOrEmpty = linkingKeys.findByLinkingKey(linkingKey);
        if (linkingKeyOrEmpty.isEmpty()) {
            return new WalletUser(new LinkingKey(linkingKey));
        }

        return users.findByLinkingKey(linkingKeyOrEmpty.get())
                .orElseThrow(() -> new EmptyResultDataAccessException("WalletUser key not found", 1));
    }

    private boolean verifyLogin(byte[] linkingKey, byte[] signature, K1 k1) {
        ByteVector rawK1 = ByteVector.view(k1.data());
        ByteVector64 rawSig = Crypto.der2compact(ByteVector.view(signature));
        Crypto.PublicKey rawKey = new Crypto.PublicKey(ByteVector.view(linkingKey));

        return Crypto.verifySignature(rawK1, rawSig, rawKey);
    }
}
